package com.cheeeese.global.message;

import com.solapi.sdk.message.exception.SolapiEmptyResponseException;
import com.solapi.sdk.message.exception.SolapiMessageNotReceivedException;
import com.solapi.sdk.message.exception.SolapiUnknownException;
import com.solapi.sdk.message.model.Message;
import com.solapi.sdk.message.service.DefaultMessageService;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SolapiKakaoMessageSender implements KakaoMessageSender {

    private final DefaultMessageService messageService;

    @Override
    @RateLimiter(name = "kakaoMessage")
    public void send(Message message) throws
            SolapiMessageNotReceivedException,
            SolapiEmptyResponseException,
            SolapiUnknownException
    {
        messageService.send(message);
    }

    @Override
    @RateLimiter(name = "kakaoMessage")
    public void sendAll(List<Message> messages) throws
            SolapiMessageNotReceivedException,
            SolapiEmptyResponseException,
            SolapiUnknownException
    {
        messageService.send(messages);
    }
}
