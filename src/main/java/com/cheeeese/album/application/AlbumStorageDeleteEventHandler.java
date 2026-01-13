package com.cheeeese.album.application;

import com.cheeeese.album.domain.StorageDeleteOutbox;
import com.cheeeese.album.infrastructure.persistence.StorageDeleteOutboxRepository;
import com.cheeeese.global.util.ObjectStorageDeleteUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.event.TransactionPhase;

@Slf4j
@Component
@RequiredArgsConstructor
public class AlbumStorageDeleteEventHandler {

    private final ObjectStorageDeleteUtil objectStorageDeleteUtil;
    private final StorageDeleteOutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Retryable(
            retryFor = Exception.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 500, multiplier = 2.0) // 0.5s -> 1s -> 2s
    )
    public void handle(AlbumStorageDeleteEvent event) {
        Long albumId = event.albumId();

        // 1) 삭제된 Photo들의 원본/썸네일 삭제
        for (AlbumStorageDeleteEvent.PhotoObjectDeleteTarget t : event.photoObjectTargets()) {
            objectStorageDeleteUtil.deletePhotoObjectsStrict(
                    t.imageUrl(),
                    t.thumbnailUrl(),
                    t.deleteOriginal()
            );
        }

        log.info("[AlbumExpiration][StorageDelete] albumId={} async delete completed (photoObjects={})",
                albumId, event.photoObjectTargets().size());
    }

    /**
     * 3회 다 실패한 경우
     * - Outbox에 이벤트 payload 저장
     */
    @Recover
    public void recover(Exception e, AlbumStorageDeleteEvent event) {
        Long albumId = event.albumId();

        String payloadJson = safeToJson(event);
        String reason = (e.getMessage() == null) ? e.getClass().getSimpleName() : e.getMessage();

        outboxRepository.save(StorageDeleteOutbox.of(albumId, payloadJson, reason));
        log.error("[AlbumExpiration][StorageDelete][OUTBOX] albumId={} saved to outbox. reason={}", albumId, reason, e);
    }

    private String safeToJson(AlbumStorageDeleteEvent event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException ex) {
            // JSON 변환 실패는 payload 최소화해서 남김
            return "{\"albumId\":" + event.albumId() + ",\"photoObjectTargetsCount\":" + event.photoObjectTargets().size() + "}";
        }
    }
}
