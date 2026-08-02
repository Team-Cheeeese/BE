package com.cheeeese.photo.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class PhotoCacheInvalidationEventHandler {

    private final PhotoCacheInvalidator photoCacheInvalidator;

    @Retryable(
            retryFor = Exception.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 500, multiplier = 2.0)
    )
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(PhotoCacheInvalidationEvent event) {
        photoCacheInvalidator.invalidate(event.albumCode());
    }

    @Recover
    public void recover(Exception e, PhotoCacheInvalidationEvent event) {
        log.error("[Redis][CacheInvalidation] Failed after retries. albumCode={}", event.albumCode(), e);
    }
}
