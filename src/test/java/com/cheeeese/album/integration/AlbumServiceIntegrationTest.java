package com.cheeeese.album.integration;

import com.cheeeese.album.application.AlbumService;
import com.cheeeese.album.domain.Album;
import com.cheeeese.album.domain.UserAlbum;
import com.cheeeese.album.domain.type.Role;
import com.cheeeese.album.infrastructure.persistence.AlbumRepository;
import com.cheeeese.album.infrastructure.persistence.UserAlbumRepository;
import com.cheeeese.fixture.FixtureFactory;
import com.cheeeese.photo.domain.Photo;
import com.cheeeese.photo.infrastructure.persistence.PhotoLikesRepository;
import com.cheeeese.photo.infrastructure.persistence.PhotoRepository;
import com.cheeeese.user.domain.User;
import com.cheeeese.user.infrastructure.persistence.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class AlbumServiceIntegrationTest {

    @Autowired
    AlbumService albumService;

    @Autowired
    AlbumRepository albumRepository;

    @Autowired
    UserAlbumRepository userAlbumRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PhotoRepository photoRepository;

    @Autowired
    PhotoLikesRepository photoLikesRepository;

    @Test
    @DisplayName("MAKER가 다른 참여자를 블랙 리스트하면 참여자 수와 사용자 통계가 감소한다.")
    void blacklistUser_success() {
        // given
        User maker = userRepository.save(FixtureFactory.createKakaoUser());
        User target = userRepository.save(FixtureFactory.createKakaoTarget());

        Album album = albumRepository.save(FixtureFactory.createAlbum(maker.getId()));

        userAlbumRepository.save(
                FixtureFactory.createHostUserAlbum(maker, album)
        );
        userAlbumRepository.save(
                FixtureFactory.createGuestUserAlbum(target, album)
        );
        albumRepository.incrementParticipantCountAtomically(album.getId());

        int uploadedPhotoCount = 3;
        int likePerPhoto = 1;

        List<Photo> photos = new ArrayList<>();
        for (int i = 0; i < uploadedPhotoCount; i++) {
            Photo photo = photoRepository.save(
                    FixtureFactory.createPhoto(target, album, LocalDateTime.now())
            );
            photos.add(photo);

            photoLikesRepository.save(FixtureFactory.createPhotoLikes(target, photo));
        }
        userRepository.incrementPhotoCount(target.getId(), uploadedPhotoCount);

        for (int i = 0; i < uploadedPhotoCount * likePerPhoto; i++) {
            userRepository.incrementLikeCnt(target.getId());
        }

        // before 값 저장
        Album beforeAlbum = albumRepository.findById(album.getId()).orElseThrow();
        User beforeTarget = userRepository.findById(target.getId()).orElseThrow();

        int beforeCurrentParticipantCnt = beforeAlbum.getCurrentParticipant();
        int beforeAlbumCnt = beforeTarget.getAlbumCnt();
        int beforePhotoCnt = beforeTarget.getPhotoCnt();
        int beforeLikesCnt = beforeTarget.getLikesCnt();

        // when
        albumService.blacklistUser(maker, album.getCode(), target.getId());

        // then
        Album afterAlbum = albumRepository.findById(album.getId()).orElseThrow();

        UserAlbum targetAlbum = userAlbumRepository.findByUserIdAndAlbumId(
                target.getId(), album.getId()
        ).orElseThrow();

        User afterTarget = userRepository.findById(target.getId()).orElseThrow();

        // 앨범 참여자 수 감소
        assertThat(afterAlbum.getCurrentParticipant())
                .isEqualTo(beforeCurrentParticipantCnt - 1);

        // UserAlbum 상태 변경
        assertThat(targetAlbum.getRole()).isEqualTo(Role.BLACK);

        // 사용자 통계 감소
        assertThat(afterTarget.getAlbumCnt())
                .isEqualTo(beforeAlbumCnt - 1);

        assertThat(afterTarget.getPhotoCnt())
                .isEqualTo(beforePhotoCnt - uploadedPhotoCount);

        assertThat(afterTarget.getLikesCnt())
                .isEqualTo(
                        Math.max(beforeLikesCnt - (uploadedPhotoCount * likePerPhoto), 0)
                );

        // 사진 삭제 확인
        assertThat(
                photoRepository.findIdsByAlbumIdAndUserId(album.getId(), target.getId())
        ).isEmpty();
    }
}
