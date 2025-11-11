package com.cheeeese.album.application;

import com.cheeeese.album.infrastructure.persistence.AlbumExpirationRedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class AlbumExpirationScheduler {

    private final AlbumExpirationRedisRepository albumExpirationRedisRepository;
    private final AlbumExpirationService albumExpirationService;

    @Scheduled(fixedDelay = 1000L)
    public void handleAlbumExpirations() {
        Set<Long> trackedAlbumIds = albumExpirationRedisRepository.getTrackedAlbumIds();

        if (trackedAlbumIds.isEmpty()) {
            return;
        }

        for (Long albumId : trackedAlbumIds) {
            if (!albumExpirationRedisRepository.isExpired(albumId)) {
                continue;
            }

            try {
                albumExpirationService.expireAlbum(albumId);
                albumExpirationRedisRepository.unregister(albumId);
            } catch (Exception exception) {
                log.error("[AlbumExpiration] Failed to process album id={}", albumId, exception);
            }
        }
    }
}