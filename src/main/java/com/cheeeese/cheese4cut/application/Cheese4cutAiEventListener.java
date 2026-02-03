package com.cheeeese.cheese4cut.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class Cheese4cutAiEventListener {

    private final Cheese4cutAiService aiService;

    @Async("cheeseAsyncExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleCheese4cutFinalized(Cheese4cutFinalizedEvent event) {
        log.info("AI 비동기 파이프라인 시작 - cheese4cutId={}", event.cheese4cut().getId());

        aiService.generateAiSummary(
                event.cheese4cut(),
                event.album(),
                event.photos()
        );
    }
}
