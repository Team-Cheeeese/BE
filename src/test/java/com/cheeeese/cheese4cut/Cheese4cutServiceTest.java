package com.cheeeese.cheese4cut;

import com.cheeeese.album.application.validator.AlbumValidator;
import com.cheeeese.album.domain.Album;
import com.cheeeese.album.infrastructure.persistence.AlbumRepository;
import com.cheeeese.cheese4cut.application.Cheese4cutService;
import com.cheeeese.cheese4cut.application.validator.Cheese4cutValidator;
import com.cheeeese.cheese4cut.domain.Cheese4cut;
import com.cheeeese.cheese4cut.dto.request.Cheese4cutFixedRequest;
import com.cheeeese.cheese4cut.dto.response.Cheese4cutPreviewResponse;
import com.cheeeese.cheese4cut.dto.response.Cheese4cutResponse;
import com.cheeeese.cheese4cut.exception.Cheese4cutException;
import com.cheeeese.cheese4cut.exception.code.Cheese4cutErrorCode;
import com.cheeeese.cheese4cut.infrastructure.persistence.Cheese4cutRepository;
import com.cheeeese.global.util.resolver.CdnUrlResolver;
import com.cheeeese.photo.domain.Photo;
import com.cheeeese.photo.domain.PhotoStatus;
import com.cheeeese.photo.infrastructure.persistence.PhotoLikesRepository;
import com.cheeeese.photo.infrastructure.persistence.PhotoRepository;
import com.cheeeese.user.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("benchmark")
class Cheese4cutServiceTest {

    @InjectMocks
    private Cheese4cutService cheese4cutService;

    @Mock private Cheese4cutRepository cheese4cutRepository;
    @Mock private AlbumValidator albumValidator;
    @Mock private Cheese4cutValidator cheese4cutValidator;
    @Mock private PhotoRepository photoRepository;
    @Mock private PhotoLikesRepository photoLikesRepository;
    @Mock private AlbumRepository albumRepository;
    @Mock private CdnUrlResolver cdnUrlResolver;

    @Test
    @DisplayName("치즈네컷 수동 확정 시 정상적으로 엔티티가 저장된다.")
    void finalizeCheese4cut_Success() {
        // given
        String code = "valid-code";
        User maker = mock(User.class);
        Album album = mock(Album.class);
        List<Long> photoIds = List.of(1L, 2L, 3L, 4L);
        Cheese4cutFixedRequest request = new Cheese4cutFixedRequest(photoIds);

        // Mocking
        given(albumValidator.validateAlbumCode(code)).willReturn(album);
        given(album.isExpired()).willReturn(false);
        // 이미 확정된 내역이 없어야 함
        given(cheese4cutRepository.findByAlbumId(album.getId())).willReturn(Optional.empty());

        // 사진 조회 결과 Mocking (순서대로 조회되었다고 가정)
        Photo p1 = mock(Photo.class); given(p1.getId()).willReturn(1L);
        Photo p2 = mock(Photo.class); given(p2.getId()).willReturn(2L);
        Photo p3 = mock(Photo.class); given(p3.getId()).willReturn(3L);
        Photo p4 = mock(Photo.class); given(p4.getId()).willReturn(4L);

        given(photoRepository.findAllByIdInOrderByLikesDescCreatedDesc(photoIds))
                .willReturn(List.of(p1, p2, p3, p4));

        // when
        cheese4cutService.finalizeCheese4cut(maker, code, request);

        // then
        // 1. 권한 검증 호출 여부
        verify(cheese4cutValidator).validateUserIsMaker(album, maker);
        // 2. 사진 유효성 검증 호출 여부
        verify(cheese4cutValidator).validateFinalizePhotos(album, photoIds);
        // 3. 최종 저장 호출 여부
        verify(cheese4cutRepository).save(any(Cheese4cut.class));
    }

    @Test
    @DisplayName("치즈네컷 확정 전, 좋아요 상위 4개 사진을 미리보기로 반환한다.")
    void getCheese4cut_Preview_Success() {
        // given
        String code = "preview-code";
        User user = mock(User.class);
        Album album = mock(Album.class);
        given(album.getId()).willReturn(1L);
        given(album.getParticipant()).willReturn(10);

        given(albumRepository.findByCode(code)).willReturn(Optional.of(album));

        // 확정 내역 없음
        given(cheese4cutRepository.findByAlbumId(album.getId())).willReturn(Optional.empty());

        // 좋아요 상위 4개 사진 ID 조회 Mock
        List<Long> topIds = List.of(10L, 20L, 30L, 40L);
        given(photoRepository.findTop4CompletedPhotoIdsByLikes(eq(1L), eq(PhotoStatus.COMPLETED), any()))
                .willReturn(topIds);

        // 사진 객체 조회 Mock (순서 보장을 위해 실제 리스트 반환)
        Photo p1 = mock(Photo.class); given(p1.getId()).willReturn(10L);
        Photo p2 = mock(Photo.class); given(p2.getId()).willReturn(20L);
        Photo p3 = mock(Photo.class); given(p3.getId()).willReturn(30L);
        Photo p4 = mock(Photo.class); given(p4.getId()).willReturn(40L);

        given(photoRepository.findAllByIdIn(topIds)).willReturn(List.of(p1, p2, p3, p4));
        given(cdnUrlResolver.resolveOriginal(any())).willReturn("http://cdn.url/image.jpg");
        given(photoLikesRepository.countDistinctUserIdsByPhotoIds(topIds)).willReturn(5L);
        // when
        Cheese4cutResponse response = cheese4cutService.getCheese4cutByAlbumCode(null, code);

        // then
        assertThat(response).isInstanceOf(Cheese4cutPreviewResponse.class);
        Cheese4cutPreviewResponse preview = (Cheese4cutPreviewResponse) response;

        assertThat(preview.isFinalized()).isFalse();
        assertThat(preview.previewPhotos()).hasSize(4);
        assertThat(preview.uniqueLikesCount()).isEqualTo(5);
    }

    @Test
    @DisplayName("이미 치즈네컷이 확정된 앨범에 대해 다시 확정을 시도하면 예외가 발생한다.")
    void finalizeCheese4cut_Fail_AlreadyFinalized() {
        // given
        String code = "code";
        User user = mock(User.class);
        Album album = mock(Album.class);
        given(album.getId()).willReturn(1L);

        given(albumValidator.validateAlbumCode(code)).willReturn(album);
        given(album.isExpired()).willReturn(false);

        // 이미 확정된 내역이 존재함 (Optional.of)
        given(cheese4cutRepository.findByAlbumId(album.getId()))
                .willReturn(Optional.of(mock(Cheese4cut.class)));

        Cheese4cutFixedRequest request = new Cheese4cutFixedRequest(List.of(1L, 2L, 3L, 4L));

        // when & then
        assertThatThrownBy(() -> cheese4cutService.finalizeCheese4cut(user, code, request))
                .isInstanceOf(Cheese4cutException.class)
                .hasFieldOrPropertyWithValue("errorCode", Cheese4cutErrorCode.CHEESE4CUT_ALREADY_FINALIZED);
    }

    @Test
    @DisplayName("확정 전 미리보기 조회 시, 완료된 사진이 4장 미만이면 예외가 발생한다.")
    void getCheese4cut_Preview_Fail_InsufficientPhotos() {
        // given
        String code = "code";
        Album album = mock(Album.class);
        given(album.getId()).willReturn(1L);
        given(albumRepository.findByCode(code)).willReturn(Optional.of(album));

        // 확정 내역 없음
        given(cheese4cutRepository.findByAlbumId(1L)).willReturn(Optional.empty());

        // 사진 조회 결과가 3장뿐임
        given(photoRepository.findTop4CompletedPhotoIdsByLikes(eq(1L), eq(PhotoStatus.COMPLETED), any()))
                .willReturn(List.of(1L, 2L, 3L));

        // when & then
        assertThatThrownBy(() -> cheese4cutService.getCheese4cutByAlbumCode(null, code))
                .isInstanceOf(Cheese4cutException.class)
                .hasFieldOrPropertyWithValue("errorCode", Cheese4cutErrorCode.INSUFFICIENT_COUNT_FOR_CHEESE4CUT);
    }
}