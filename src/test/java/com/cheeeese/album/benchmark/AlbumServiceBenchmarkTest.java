package com.cheeeese.album.benchmark;

import com.cheeeese.album.domain.Album;
import com.cheeeese.album.infrastructure.persistence.AlbumRepository;
import com.cheeeese.fixture.FixtureFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@SpringBootTest
@Transactional
@Tag("benchmark")
public class AlbumServiceBenchmarkTest {

    @Autowired
    private AlbumRepository albumRepository;

    @PersistenceContext
    private EntityManager em;

    private static final int DATA_SIZE = 20000;
    private final Random random = new Random();

    @BeforeEach
    void setUp() {
        System.out.println("UUID v4Codes VS UUID v7Codes 성능 테스트를 위한 데이터 삽입 시작");

        List<Album> v4Albums = new ArrayList<>();
        List<Album> v7Albums = new ArrayList<>();

        for (int i = 0; i < DATA_SIZE; i++) {
            v4Albums.add(FixtureFactory.createAlbumV4(i));
            v7Albums.add(FixtureFactory.createAlbumV7(i));
        }

        long startV4 = System.currentTimeMillis();
        albumRepository.saveAll(v4Albums);
        albumRepository.flush();
        long v4InsertTime = System.currentTimeMillis() - startV4;

        long startV7 = System.currentTimeMillis();
        albumRepository.saveAll(v7Albums);
        albumRepository.flush();
        long v7InsertTime = System.currentTimeMillis() - startV7;

        System.out.printf("Insert 완료 (v4: %d ms, v7: %d ms)%n", v4InsertTime, v7InsertTime);
    }

    @Test
    @DisplayName("UUID v4 vs v7 전체 DB 성능 테스트")
    void compareFullPerformance() {
        List<Album> allAlbums = albumRepository.findAll();

        List<String> v4Codes = allAlbums.stream()
                .filter(a -> a.getTitle().startsWith("v4"))
                .map(Album::getCode)
                .toList();

        List<String> v7Codes = allAlbums.stream()
                .filter(a -> a.getTitle().startsWith("v7"))
                .map(Album::getCode)
                .toList();

        // 전체 조회 속도 테스트
        measureSelectingTest("v4", v4Codes);
        measureSelectingTest("v7", v7Codes);

        // 전체 정렬 시간 테스트
        measureSortingTest("v4");
        measureSortingTest("v7");
    }

    private void measureSelectingTest(String label, List<String> codes) {
        long total = 0;
        for (int i = 0; i < 100; i++) {
            String code = codes.get(random.nextInt(codes.size()));
            long start = System.nanoTime();
            albumRepository.findByCode(code);
            total += System.nanoTime() - start;
        }
        System.out.printf("[%s] 전체 조회 시간: %.2f ms%n", label, total / 1000000.0);
    }

    private void measureSortingTest(String label) {
        long start = System.currentTimeMillis();
        em.createQuery("""
                SELECT a
                FROM Album a
                WHERE a.title LIKE :prefix
                ORDER BY a.code DESC
            """, Album.class)
                .setParameter("prefix", label + "%")
                .getResultList();
        long elapsed = System.currentTimeMillis() - start;
        System.out.printf("[%s] Order By 정렬 시간: %d ms%n", label, elapsed);
    }
}
