package com.cheeeese.global.config;

import com.cheeeese.global.message.FakeKakaoMessageSender;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class FakeKakaoMessageSenderConfig {

    @Bean
    @Primary
    public FakeKakaoMessageSender fakeKakaoMessageSender() {
        return new FakeKakaoMessageSender();
    }
}
