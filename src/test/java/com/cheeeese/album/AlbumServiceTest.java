package com.cheeeese.album;

import com.cheeeese.album.application.AlbumService;
import com.cheeeese.album.application.validator.AlbumValidator;
import com.cheeeese.album.domain.Album;
import com.cheeeese.album.domain.UserAlbum;
import com.cheeeese.album.domain.type.AlbumJoinStatus;
import com.cheeeese.album.dto.response.AlbumEnterResponse;
import com.cheeeese.album.dto.response.ExistingEnterResponse;
import com.cheeeese.album.dto.response.NewEnterResponse;
import com.cheeeese.album.exception.AlbumException;
import com.cheeeese.album.exception.code.AlbumErrorCode;
import com.cheeeese.album.infrastructure.persistence.AlbumExpirationRedisRepository;
import com.cheeeese.album.infrastructure.persistence.AlbumRepository;
import com.cheeeese.album.infrastructure.persistence.UserAlbumRepository;
import com.cheeeese.global.util.resolver.CdnUrlResolver;
import com.cheeeese.photo.application.PhotoService;
import com.cheeeese.photo.infrastructure.persistence.PhotoLikesRepository;
import com.cheeeese.photo.infrastructure.persistence.PhotoRepository;
import com.cheeeese.user.domain.User;
import com.cheeeese.user.infrastructure.persistence.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("benchmark")
class AlbumServiceTest {

    private AlbumService albumService;

    // 모든 의존성 Mock 선언 (PhotoService 포함)
    @Mock private AlbumValidator albumValidator;
    @Mock private AlbumRepository albumRepository;
    @Mock private UserAlbumRepository userAlbumRepository;
    @Mock private UserRepository userRepository;
    @Mock private PhotoRepository photoRepository;
    @Mock private PhotoLikesRepository photoLikesRepository;
    @Mock private PhotoService photoService;
    @Mock private AlbumExpirationRedisRepository albumExpirationRedisRepository;
    @Mock private CdnUrlResolver cdnUrlResolver;

    @BeforeEach
    void setUp() {
        // 생성자를 통해 직접 주입
        albumService = new AlbumService(
                albumValidator,
                albumRepository,
                userAlbumRepository,
                userRepository,
                photoRepository,
                photoLikesRepository,
                photoService,
                albumExpirationRedisRepository,
                cdnUrlResolver
        );
    }

    @Test
    @DisplayName("신규 참여자는 앨범 입장 시 GUEST 권한을 얻고 참여자 수가 증가한다.")
    void enterAlbum_NewUser() {
        // given
        String code = "album-code-123";

        User user = mock(User.class);
        given(user.getId()).willReturn(10L);

        User maker = mock(User.class);
        given(maker.getId()).willReturn(100L);

        Album album = Album.builder()
                .makerId(maker.getId())
                .title("Test Album")
                .themeEmoji("🧀")
                .eventDate(LocalDate.now())
                .expiredAt(LocalDateTime.now().plusDays(7))
                .build();

        // 1. Validator 통과 설정
        given(albumValidator.validateAlbumCode(code)).willReturn(album);
        doNothing().when(albumValidator).validateAlbumEntry(album, user);
        doNothing().when(albumValidator).validateAlbumCapacity(album);

        // 2. 기존 참여 이력 없음 (신규)
        given(userAlbumRepository.findByUserIdAndAlbumId(user.getId(), album.getId()))
                .willReturn(Optional.empty());

        // 3. Maker 정보 조회
        given(userRepository.findById(album.getMakerId())).willReturn(Optional.of(maker));

        // 4. 참여자 수 증가 성공
        given(albumRepository.incrementParticipantCountAtomically(album.getId())).willReturn(1);

        // 5. [중요] PhotoService 호출 시 빈 리스트 반환 (NPE 방지)
        given(photoService.getRecentPhotosForNewEnter(album.getId())).willReturn(List.of());

        // when
        AlbumEnterResponse response = albumService.enterAlbum(code, user);

        // then
        assertThat(response).isInstanceOf(NewEnterResponse.class);
        assertThat(response.joinStatus()).isEqualTo(AlbumJoinStatus.NEW);

        verify(userAlbumRepository).save(any(UserAlbum.class));
        verify(albumRepository).incrementParticipantCountAtomically(album.getId());
    }

    @Test
    @DisplayName("기존에 나갔던 참여자가 재입장(REJOINED)하면 isVisible이 true로 변경된다.")
    void enterAlbum_RejoinedUser() {
        // given
        String code = "album-code-rejoin";
        User user = mock(User.class);
        given(user.getId()).willReturn(20L);

        User maker = mock(User.class);
        given(maker.getId()).willReturn(100L);

        Album album = Album.builder()
                .makerId(maker.getId())
                .title("Rejoin Album")
                .themeEmoji("📸")
                .eventDate(LocalDate.now())
                .expiredAt(LocalDateTime.now().plusDays(7))
                .build();

        given(albumValidator.validateAlbumCode(code)).willReturn(album);
        doNothing().when(albumValidator).validateAlbumEntry(album, user);

        // 기존 참여 정보 존재 (isVisible=false) -> 재입장 케이스
        UserAlbum userAlbum = mock(UserAlbum.class);
        given(userAlbum.isVisible()).willReturn(false);

        given(userAlbumRepository.findByUserIdAndAlbumId(user.getId(), album.getId()))
                .willReturn(Optional.of(userAlbum));

        given(userRepository.findById(album.getMakerId())).willReturn(Optional.of(maker));

        // when
        AlbumEnterResponse response = albumService.enterAlbum(code, user);

        // then
        assertThat(response).isInstanceOf(ExistingEnterResponse.class);
        assertThat(response.joinStatus()).isEqualTo(AlbumJoinStatus.REJOINED);

        verify(userAlbum).show();
    }

    @Test
    @DisplayName("앨범 정원이 초과된 경우 입장이 거부되고 예외가 발생한다.")
    void enterAlbum_Fail_MaxParticipantReached() {
        // given
        String code = "full-album-code";
        User user = mock(User.class);
        Album album = mock(Album.class);

        Long makerId = 999L;
        given(album.getMakerId()).willReturn(makerId);

        given(albumValidator.validateAlbumCode(code)).willReturn(album);
        doNothing().when(albumValidator).validateAlbumEntry(album, user);

        // 기존 참여자가 아님
        given(userAlbumRepository.findByUserIdAndAlbumId(user.getId(), album.getId()))
                .willReturn(Optional.empty());

        // Maker 정보 조회 Mocking 추가
        given(userRepository.findById(makerId)).willReturn(Optional.of(mock(User.class)));

        // 정원 체크 통과 가정
        doNothing().when(albumValidator).validateAlbumCapacity(album);

        // 핵심: 업데이트 된 행의 개수가 0 (업데이트 실패 = 정원 초과)
        given(albumRepository.incrementParticipantCountAtomically(album.getId())).willReturn(0);

        // when & then
        assertThatThrownBy(() -> albumService.enterAlbum(code, user))
                .isInstanceOf(AlbumException.class)
                .hasFieldOrPropertyWithValue("errorCode", AlbumErrorCode.ALBUM_MAX_PARTICIPANT_REACHED);
    }
}