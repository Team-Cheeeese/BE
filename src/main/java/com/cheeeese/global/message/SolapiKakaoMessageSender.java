package com.cheeeese.global.message;

import com.solapi.sdk.message.exception.SolapiEmptyResponseException;
import com.solapi.sdk.message.exception.SolapiMessageNotReceivedException;
import com.solapi.sdk.message.exception.SolapiUnknownException;
import com.solapi.sdk.message.model.Message;
import com.solapi.sdk.message.service.DefaultMessageService;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SolapiKakaoMessageSender implements KakaoMessageSender {

    private final DefaultMessageService messageService;

    @Override
    @RateLimiter(name = "kakaoMessage")
    @Retryable(
            retryFor = { SolapiMessageNotReceivedException.class, SolapiEmptyResponseException.class, SolapiUnknownException.class },
            noRetryFor = { RequestNotPermitted.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    public void send(Message message) throws
            SolapiMessageNotReceivedException,
            SolapiEmptyResponseException,
            SolapiUnknownException
    {
        messageService.send(message);
    }

    @Override
    @RateLimiter(name = "kakaoMessage")
    @Retryable(
            retryFor = { SolapiMessageNotReceivedException.class, SolapiEmptyResponseException.class, SolapiUnknownException.class },
            noRetryFor = { RequestNotPermitted.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    public void sendAll(List<Message> messages) throws
            SolapiMessageNotReceivedException,
            SolapiEmptyResponseException,
            SolapiUnknownException
    {
        messageService.send(messages);
    }

    @Recover
    public void recoverSend(Exception e, Message message) {
        log.error("[Solapi] 3회 재시도 후 최종 발송 실패. to={}, cause={}", message.getTo(), e.toString());
    }

    @Recover
    public void recoverSendAll(Exception e, List<Message> messages) {
        log.error("[Solapi] 3회 재시도 후 최종 발송 실패(벌크). 대상 수={}, cause={}", messages.size(), e.toString());
    }
}
