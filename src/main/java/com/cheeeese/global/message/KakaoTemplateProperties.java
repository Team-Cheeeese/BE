package com.cheeeese.global.message;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "solapi.template")
public record KakaoTemplateProperties(
        String albumJoined,
        String cheese4cutCreated,
        String albumExpireD1
) {
}
