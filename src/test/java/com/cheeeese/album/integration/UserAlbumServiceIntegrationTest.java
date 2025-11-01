package com.cheeeese.album.integration;

import com.cheeeese.album.domain.Album;
import com.cheeeese.album.domain.UserAlbum;
import com.cheeeese.album.domain.type.Role;
import com.cheeeese.album.infrastructure.persistence.AlbumRepository;
import com.cheeeese.album.infrastructure.persistence.UserAlbumRepository;
import com.cheeeese.fixture.FixtureFactory;
import com.cheeeese.user.domain.User;
import com.cheeeese.user.infrastructure.persistence.UserRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class UserAlbumServiceIntegrationTest {

    @Autowired
    private AlbumRepository albumRepository;

    @Autowired
    private UserAlbumRepository userAlbumRepository;

    private static final int ITERATIONS = 1000;

    private static User testUser;
    private static Album testAlbum;

    @BeforeAll
    static void setUp(
            @Autowired UserRepository userRepository,
            @Autowired AlbumRepository albumRepository,
            @Autowired UserAlbumRepository userAlbumRepository
    ) {
        testUser = FixtureFactory.createKakaoUser();
        userRepository.save(testUser);

        testAlbum = FixtureFactory.createAlbum(testUser.getId());
        albumRepository.save(testAlbum);

        UserAlbum userAlbum = FixtureFactory.createHostUserAlbum(testUser, testAlbum);
        userAlbumRepository.save(userAlbum);

        System.out.println("[테스트 데이터 생성 완료]");
    }

    @Test
    @DisplayName("JOIN 조회 vs 직접 조회 성능 테스트")
    void compareJoinQueryAndDirectQueryPerformance() {
        for (int i = 0; i < 5; i++) {
            albumRepository.findByMakerId(testAlbum.getMakerId());
            userAlbumRepository.findByAlbumIdAndUserIdAndRole(testAlbum.getId(), testUser.getId(), Role.MAKER);
        }

        long total1 = 0;
        long total2 = 0;

        for (int i = 0; i < ITERATIONS; i++) {
            long start = System.nanoTime();
            albumRepository.findByMakerId(testAlbum.getMakerId());
            total1 += System.nanoTime() - start;

            start = System.nanoTime();
            userAlbumRepository.findByAlbumIdAndUserIdAndRole(testAlbum.getId(), testUser.getId(), Role.MAKER);
            total2 += System.nanoTime() - start;
        }

        System.out.printf("[1] Album.hostId 직접 조회 평균: %.2f ms%n", (total1 / 1_000_000.0 / ITERATIONS));
        System.out.printf("[2] Participant JOIN 조회 평균: %.2f ms%n", (total2 / 1_000_000.0 / ITERATIONS));
    }
}
