package com.cheeeese.album.application.scheduler;

import com.cheeeese.album.domain.Album;
import com.cheeeese.album.domain.event.AlbumExpireD1Event;
import com.cheeeese.album.infrastructure.persistence.AlbumRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AlbumReminderScheduler {

    private final AlbumRepository albumRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Scheduled(cron = "0 0 8 * * *")
    public void notifyExpireD1Albums() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);

        LocalDateTime start = tomorrow.atStartOfDay();

        LocalDateTime end = tomorrow.plusDays(1).atStartOfDay();

        List<Album> albums = albumRepository.findAlbumsExpiringBetween(
                Album.AlbumStatus.ACTIVE, start, end
        );

        log.info("[AlbumReminder] D-1 대상 앨범 수={}", albums.size());

        for (Album album : albums) {
            try {
                applicationEventPublisher.publishEvent(
                        AlbumExpireD1Event.of(album.getId())
                );
            } catch (Exception e) {
                log.error("[AlbumReminder] D-1 이벤트 발행 실패. album_id={}", album.getId(), e);
            }
        }
    }
}
