package com.cheeeese.photo;

import com.cheeeese.album.domain.Album;
import com.cheeeese.album.domain.type.AlbumSorting;
import com.cheeeese.album.infrastructure.persistence.AlbumRepository;
import com.cheeeese.fixture.FixtureFactory;
import com.cheeeese.photo.application.PhotoCacheInvalidator;
import com.cheeeese.photo.application.PhotoQueryService;
import com.cheeeese.photo.domain.Photo;
import com.cheeeese.photo.infrastructure.persistence.PhotoRepository;
import com.cheeeese.user.domain.User;
import com.cheeeese.user.infrastructure.persistence.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Tag("benchmark")
@Transactional
@SpringBootTest
public class PhotoQueryPerformanceTest {

    @Autowired
    private PhotoQueryService photoQueryService;

    @Autowired
    private PhotoCacheInvalidator photoCacheInvalidator;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AlbumRepository albumRepository;

    @Autowired
    private PhotoRepository photoRepository;

    private User testUser;
    private Album testAlbum;

    @BeforeEach
    void setUp() {
        // 1. 테스트 유저 및 앨범 생성
        testUser = userRepository.save(FixtureFactory.createKakaoUser());
        testAlbum = albumRepository.save(FixtureFactory.createAlbum(testUser.getId()));

        // 2. 2000장의 사진 데이터 삽입
        List<Photo> photos = new ArrayList<>();
        for (int i = 0; i < 2000; i++) {
            photos.add(FixtureFactory.createCompletedPhoto(testUser, testAlbum, LocalDateTime.now()));
        }
        photoRepository.saveAll(photos);

        // 3. 기존 캐시가 있다면 무효화
        photoCacheInvalidator.invalidate(testAlbum.getCode());
    }

    @Test
    @DisplayName("2000장 기준 DB vs 캐시 조회 성능 비교")
    void comparePerformanceWith2000Photos() {
        StopWatch stopWatch = new StopWatch("Photo Query Performance Test (2000 Photos)");

        // DB 조회 (Cache Miss & Save)
        stopWatch.start("DB Query (Cold Start)");
        photoQueryService.getPhotoPage(testUser, testAlbum.getCode(), 0, 2000, AlbumSorting.CREATED_AT);
        stopWatch.stop();

        // Redis 조회 (Cache Hit)
        stopWatch.start("Redis Cache (Warm Start)");
        photoQueryService.getPhotoPage(testUser, testAlbum.getCode(), 0, 2000, AlbumSorting.CREATED_AT);
        stopWatch.stop();

        // 결과 출력
        System.out.println(stopWatch.prettyPrint());

        double dbTime = stopWatch.getTaskInfo()[0].getTimeSeconds();
        double cacheTime = stopWatch.getTaskInfo()[1].getTimeSeconds();

        System.out.println("---------------------------------------");
        System.out.printf("DB 조회 소요 시간: %.4f 초%n", dbTime);
        System.out.printf("캐시 조회 소요 시간: %.4f 초%n", cacheTime);
        System.out.printf("성능 개선율: %.2f%%%n", ((dbTime - cacheTime) / dbTime * 100));
        System.out.println("---------------------------------------");
    }

    @Test
    @DisplayName("64명 참여자 및 2000장 사진 조회 성능 테스트 - 앨범 중복 해결")
    void comparePerformanceWith64UsersAnd2000Photos() {
        // 1. 64명의 고유 유저 생성 (이메일/ID 중복 방지)
        List<User> participants = new ArrayList<>();
        for (int i = 1; i <= 64; i++) {
            User member = FixtureFactory.createUniqueKakaoUser(i);
            participants.add(userRepository.save(member));
        }

        // 2. 고유한 코드를 가진 앨범 생성
        User maker = participants.get(0);
        Album album = FixtureFactory.createAlbum(maker.getId());
        album = albumRepository.save(album);

        // 3. 2000장의 사진 업로드
        List<Photo> photos = new ArrayList<>();
        for (int i = 0; i < 2000; i++) {
            User uploader = participants.get(i % 64);
            photos.add(FixtureFactory.createCompletedPhoto(uploader, album, LocalDateTime.now()));
        }
        photoRepository.saveAll(photos);
        photoRepository.flush();

        // 캐시 초기화
        photoCacheInvalidator.invalidate(album.getCode());

        // 테스트 유저
        User requester = participants.get(10);

        StopWatch stopWatch = new StopWatch("Performance Test (64 Users, 2000 Photos)");

        // DB 조회
        stopWatch.start("DB Query (Cold Start)");
        photoQueryService.getPhotoPage(requester, album.getCode(), 0, 2000, AlbumSorting.CREATED_AT);
        stopWatch.stop();

        // Redis 조회
        stopWatch.start("Redis Cache Hit (Warm Start)");
        photoQueryService.getPhotoPage(requester, album.getCode(), 0, 2000, AlbumSorting.CREATED_AT);
        stopWatch.stop();

        System.out.println(stopWatch.prettyPrint());

        double dbTime = stopWatch.getTaskInfo()[0].getTimeSeconds();
        double cacheTime = stopWatch.getTaskInfo()[1].getTimeSeconds();

        System.out.println("====================================================");
        System.out.printf("대상 앨범: %s (사진 2000장, 유저 64명)%n", album.getCode());
        System.out.printf("DB 기반 처리 속도: %.4f 초%n", dbTime);
        System.out.printf("캐시 기반 처리 속도: %.4f 초%n", cacheTime);
        System.out.printf("성능 개선율: %.2f%%%n", ((dbTime - cacheTime) / dbTime * 100));
        System.out.println("====================================================");
    }
}
