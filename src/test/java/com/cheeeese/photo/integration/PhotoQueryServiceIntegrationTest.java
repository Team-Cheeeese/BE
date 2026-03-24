package com.cheeeese.photo.integration;

import com.cheeeese.album.domain.Album;
import com.cheeeese.album.domain.UserAlbum;
import com.cheeeese.album.domain.type.AlbumSorting;
import com.cheeeese.album.infrastructure.persistence.AlbumRepository;
import com.cheeeese.album.infrastructure.persistence.UserAlbumRepository;
import com.cheeeese.fixture.FixtureFactory;
import com.cheeeese.photo.application.PhotoInfoService;
import com.cheeeese.photo.application.PhotoQueryService;
import com.cheeeese.photo.domain.Photo;
import com.cheeeese.photo.domain.PhotoHistory;
import com.cheeeese.photo.domain.PhotoLikes;
import com.cheeeese.photo.dto.response.*;
import com.cheeeese.photo.infrastructure.persistence.PhotoHistoryRepository;
import com.cheeeese.photo.infrastructure.persistence.PhotoLikesRepository;
import com.cheeeese.photo.infrastructure.persistence.PhotoRepository;
import com.cheeeese.user.domain.User;
import com.cheeeese.user.infrastructure.persistence.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class PhotoQueryServiceIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AlbumRepository albumRepository;

    @Autowired
    private UserAlbumRepository userAlbumRepository;

    @Autowired
    private PhotoRepository photoRepository;

    @Autowired
    private PhotoHistoryRepository photoHistoryRepository;

    @Autowired
    private PhotoLikesRepository photoLikesRepository;

    @Autowired
    private PhotoQueryService photoQueryService;

    @Autowired
    private PhotoInfoService photoInfoService;

    private User testUser;
    private Album testAlbum;
    private UserAlbum testUserAlbum;
    private Photo testPhoto;
    private PhotoHistory testPhotoHistory;
    private PhotoLikes testPhotoLikes;

    @BeforeEach
    void setUp() {
        testUser = userRepository.save(FixtureFactory.createKakaoUser());
        testAlbum = albumRepository.save(FixtureFactory.createAlbum(testUser.getId()));
        testUserAlbum = userAlbumRepository.save(FixtureFactory.createHostUserAlbum(testUser, testAlbum));
        for (int i = 1; i <= 3; i++) {
            testPhoto = FixtureFactory.createCompletedPhoto(testUser, testAlbum, LocalDateTime.now());
            testPhoto.updateImageUrl("album/" + testAlbum.getId() + "/original/photo_" + i + ".jpg");
            photoRepository.save(testPhoto);
        }
        photoRepository.flush();
        testPhotoHistory = photoHistoryRepository.save(FixtureFactory.createPhotoHistory(testUser, testPhoto));
        testPhotoLikes = photoLikesRepository.save(FixtureFactory.createPhotoLikes(testUser, testPhoto));
    }

    @Test
    @DisplayName("사진 목록 조회 - 페이징 및 CDN URL 변환 확인")
    void getPhotoList() {
        // when
        PhotoPageResponse page = photoQueryService.getPhotoPage(
                testUser, testAlbum.getCode(), 0, 20, AlbumSorting.CREATED_AT, false
        );

        // then
        assertThat(page.responses()).hasSize(3);
        assertThat(page.responses().getFirst().imageUrl()).contains("say-cheese.edge.naverncp.com");
    }

    @Test
    @DisplayName("사진 상세 조회 테스트 - CDN URL, 최근 다운로드 여부 확인")
    void getPhotoDetail() {
        // given
        testPhoto.updateImageUrl("album/" + testAlbum.getId() + "/original/test.jpg");
        photoHistoryRepository.save(testPhotoHistory);

        // when
        PhotoDetailResponse response = photoQueryService.getPhotoDetail(testUser, testAlbum.getCode(), testPhoto.getId());

        // then
        assertThat(response.imageUrl()).contains("edge.naverncp.com");
        assertThat(response.isRecentlyDownloaded()).isTrue();
    }

    @Test
    @DisplayName("띱한 사용자 목록 조회")
    void getLikedUserList() {
        // when
        PhotoLikedUserResponse likedUsers = photoInfoService.getPhotoLikedUsers(
                testUser, testAlbum.getCode(), testPhoto.getId()
        );

        // then
        assertThat(likedUsers.photoLikers()).hasSize(1);
        assertThat(likedUsers.photoLikers().getFirst().name()).isNotBlank();
    }

    @Test
    @DisplayName("띱한 사진 목록 조회")
    void getUserLikedPhotoList() {
        // when
        PhotoLikedPageResponse page = photoQueryService.getPhotoLiked(
                testUser, testAlbum.getCode(), 0, 10
        );

        // then
        assertThat(page.responses()).hasSize(1);
        assertThat(page.responses().getFirst().imageUrl()).contains("say-cheese.edge.naverncp.com");
    }
}
