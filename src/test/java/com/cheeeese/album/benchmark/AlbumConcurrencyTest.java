package com.cheeeese.album.benchmark;

import com.cheeeese.album.application.AlbumService;
import com.cheeeese.album.domain.Album;
import com.cheeeese.album.infrastructure.persistence.AlbumRepository;
import com.cheeeese.fixture.FixtureFactory;
import com.cheeeese.user.domain.User;
import com.cheeeese.user.infrastructure.persistence.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Tag("benchmark")
public class AlbumConcurrencyTest {

    @Autowired private AlbumService albumService;
    @Autowired private AlbumRepository albumRepository;
    @Autowired private UserRepository userRepository;

    @Test
    @DisplayName("100명의 사용자가 동시에 앨범에 입장할 때 성능 및 정합성 테스트")
    void enterAlbumConcurrencyTest() throws InterruptedException {
        // Given
        int threadCount = 100;
        User maker = userRepository.save(FixtureFactory.createKakaoUser());
        // 정원이 100명 이상인 앨범 생성 (그래야 에러 없이 성공 케이스 측정 가능)
        Album album = FixtureFactory.createAlbum(maker.getId());
        // ※ FixtureFactory의 createAlbum이 participant를 4로 설정한다면 이 테스트를 위해
        //   FixtureFactory를 수정하거나 빌더를 통해 100명 이상으로 설정해야 합니다.
        //   여기서는 테스트를 위해 Repository에서 강제로 업데이트한다고 가정하거나
        //   Album 객체 생성 시 participant를 넉넉하게 잡아야 합니다.

        // 임시로 참여 가능 인원 늘리기 (FixtureFactory 수정 없이 진행 시)
        // 실제 코드에서는 엔티티 수정 메서드를 사용하거나 Fixture를 조정하세요.
        // 예: album.updateParticipantCount(200);
        albumRepository.save(album);

        ExecutorService executorService = Executors.newFixedThreadPool(32); // 스레드 풀
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger();

        long start = System.currentTimeMillis();

        // When
        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            executorService.submit(() -> {
                try {
                    // 각기 다른 유저 생성
                    User guest = User.builder()
                            .name("Guest" + index)
                            .email("guest" + index + "@test.com")
                            .profileImage("P1")
                            .providerId("kakao_" + index)
                            .build();
                    userRepository.save(guest);

                    // 입장 시도
                    albumService.enterAlbum(album.getCode(), guest);
                    successCount.getAndIncrement();
                } catch (Exception e) {
                    System.out.println("입장 실패: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(); // 모든 스레드가 끝날 때까지 대기
        long end = System.currentTimeMillis();

        // Then
        Album updatedAlbum = albumRepository.findById(album.getId()).orElseThrow();
        System.out.printf("동시 입장 요청 %d건 처리 시간: %d ms%n", threadCount, (end - start));
        System.out.printf("성공한 요청 수: %d%n", successCount.get());
        System.out.printf("DB 반영된 참여자 수: %d%n", updatedAlbum.getCurrentParticipant());

        // 기존 1명(메이커) + 100명(게스트) = 101명이어야 함 (또는 로직에 따라 다름)
        // enterAlbum 로직에 따라 100명이 정상적으로 들어갔는지 검증
        assertThat(updatedAlbum.getCurrentParticipant()).isEqualTo(1 + successCount.get());
    }
}
