package com.cheeeese.photo;

import com.cheeeese.album.application.validator.AlbumValidator;
import com.cheeeese.album.domain.Album;
import com.cheeeese.album.infrastructure.persistence.AlbumRepository;
import com.cheeeese.photo.application.PhotoService;
import com.cheeeese.photo.application.PresignedUrlService;
import com.cheeeese.photo.application.validator.PhotoValidator;
import com.cheeeese.photo.domain.Photo;
import com.cheeeese.photo.domain.PhotoStatus;
import com.cheeeese.photo.dto.request.PhotoPresignedUrlRequest;
import com.cheeeese.photo.dto.request.PhotoUploadReportRequest;
import com.cheeeese.photo.dto.response.PhotoPresignedUrlResponse;
import com.cheeeese.photo.exception.PhotoException;
import com.cheeeese.photo.exception.code.PhotoErrorCode;
import com.cheeeese.photo.infrastructure.persistence.PhotoRepository;
import com.cheeeese.user.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("benchmark")
class PhotoServiceTest {

    @InjectMocks
    private PhotoService photoService;

    @Mock private AlbumValidator albumValidator;
    @Mock private AlbumRepository albumRepository;
    @Mock private PhotoRepository photoRepository;
    @Mock private PhotoValidator photoValidator;
    @Mock private PresignedUrlService presignedUrlService;

    @Test
    @DisplayName("유효한 요청일 경우 Presigned URL 목록을 정상 반환한다.")
    void createPresignedUrls_Success() {
        // given
        User user = mock(User.class);
        ReflectionTestUtils.setField(photoService, "bucket", "test-bucket"); // @Value 주입 처리

        String code = "album-code";
        Album album = Album.builder().makerId(1L).maxPhotoCount(100).build();

        PhotoPresignedUrlRequest.FileInfo fileInfo =
                new PhotoPresignedUrlRequest.FileInfo("1.jpg", LocalDateTime.now(), 3000000, "image/jpeg");
        PhotoPresignedUrlRequest request =
                new PhotoPresignedUrlRequest(code, List.of(fileInfo));

        given(albumValidator.validateAlbumCode(code)).willReturn(album);
        given(albumRepository.findByIdForUpdate(any())).willReturn(album); // Lock 획득 모킹

        // Presigned URL 생성 모킹
        given(presignedUrlService.generatePresignedPutUrl(anyString(), anyString()))
                .willReturn("https://s3.url/upload?sig=...");

        // when
        PhotoPresignedUrlResponse response = photoService.createPresignedUrls(user, request);

        // then
        assertThat(response.presignedUrlInfos()).hasSize(1);
        assertThat(response.presignedUrlInfos().get(0).uploadUrl()).contains("https://s3.url");

        // DB에 UPLOADING 상태로 저장되었는지 검증
        verify(photoRepository).save(any(Photo.class));
        // 용량/개수 검증 로직 호출 확인
        verify(photoValidator).validatePhotoCount(anyLong(), anyInt(), anyInt());
        verify(photoValidator).validateFileInfos(any());
    }

    @Test
    @DisplayName("업로드 요청한 사진 수가 앨범의 남은 용량을 초과하면 예외가 발생한다.")
    void createPresignedUrls_Fail_MaxCountExceeded() {
        // given
        User user = mock(User.class);
        String code = "album-code";
        Album album = mock(Album.class);
        given(album.getId()).willReturn(1L);
        given(album.getMaxPhotoCount()).willReturn(100);

        given(albumValidator.validateAlbumCode(code)).willReturn(album);
        given(albumRepository.findByIdForUpdate(1L)).willReturn(album);

        // 현재 99장 있고, 2장 업로드 시도
        given(photoRepository.countActivePhotosByAlbumId(eq(1L), anyList())).willReturn(99L);

        PhotoPresignedUrlRequest request = new PhotoPresignedUrlRequest(code, List.of(
                new PhotoPresignedUrlRequest.FileInfo("1.jpg", LocalDateTime.now(), 3000000, "image/jpeg"),
                new PhotoPresignedUrlRequest.FileInfo("2.jpg", LocalDateTime.now(), 3000000, "image/jpeg")
        ));

        // Validator가 예외를 던지도록 설정 (Spy나 실제 객체 사용 시에는 로직에 따라 발생, 여기선 Mock의 행동 정의)
        doThrow(new PhotoException(PhotoErrorCode.PHOTO_MAX_COUNT_EXCEEDED))
                .when(photoValidator).validatePhotoCount(99L, 2, 100);

        // when & then
        assertThatThrownBy(() -> photoService.createPresignedUrls(user, request))
                .isInstanceOf(PhotoException.class)
                .hasFieldOrPropertyWithValue("errorCode", PhotoErrorCode.PHOTO_MAX_COUNT_EXCEEDED);
    }

    @Test
    @DisplayName("업로드 실패 보고 시, 해당 사진들의 상태가 FAILED로 변경된다.")
    void reportUploadResult_Success() {
        // given
        User user = mock(User.class);
        Long userId = 1L;
        given(user.getId()).willReturn(userId);

        List<Long> failIds = List.of(101L, 102L);
        PhotoUploadReportRequest request = new PhotoUploadReportRequest(failIds);

        // Validator 통과 설정
        PhotoValidator.ValidatedPhotos validated = new PhotoValidator.ValidatedPhotos(List.of(), 1L); // 빈 리스트라도 흐름만 검증
        given(photoValidator.validatePhotos(userId, failIds)).willReturn(validated);

        // Update 로직 실행 결과 설정 (2개 수정됨)
        given(photoRepository.updateStatusByIdsAndUserIdAndExpectedStatus(
                failIds, userId, PhotoStatus.FAILED, PhotoStatus.UPLOADING
        )).willReturn(2);

        // when
        photoService.reportUploadResult(user, request);

        // then
        // 상태 업데이트 메서드가 정확한 인자로 호출되었는지 검증
        verify(photoRepository).updateStatusByIdsAndUserIdAndExpectedStatus(
                failIds, userId, PhotoStatus.FAILED, PhotoStatus.UPLOADING
        );
    }
}