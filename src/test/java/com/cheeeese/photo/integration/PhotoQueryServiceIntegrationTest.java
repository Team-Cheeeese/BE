package com.cheeeese.photo.integration;

import com.cheeeese.album.domain.Album;
import com.cheeeese.album.infrastructure.persistence.AlbumRepository;
import com.cheeeese.fixture.FixtureFactory;
import com.cheeeese.photo.application.PhotoQueryService;
import com.cheeeese.photo.domain.Photo;
import com.cheeeese.photo.dto.response.PhotoDetailResponse;
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
    private PhotoRepository photoRepository;

    @Autowired
    private PhotoQueryService photoQueryService;

    private static User testUser;
    private static Album testAlbum;
    private static Photo testPhoto;

    @BeforeEach
    void setUp() {
        testUser = userRepository.save(FixtureFactory.createKakaoUser());
        testAlbum = albumRepository.save(FixtureFactory.createAlbum(testUser.getId()));
        testPhoto = photoRepository.save(FixtureFactory.createPhoto(testUser, testAlbum, LocalDateTime.now()));
    }

    @Test
    @DisplayName("사진 상세 조회 테스트")
    void getPhotoDetailRecentlyDownloaded() {
        // given
        testPhoto.updateImageUrl("album/" + testAlbum.getId() + "/original/test.jpg");

        // when
        PhotoDetailResponse response = photoQueryService.getPhotoDetail(testUser, testAlbum.getCode(), testPhoto.getId());

        // then
        assertThat(response.imageUrl()).contains("edge.naverncp.com");
    }
}
