package com.cheeeese.global.config;

import com.cheeeese.global.message.KakaoTemplateProperties;
import com.cheeeese.global.security.jwt.JwtProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({
        JwtProperties.class,
        KakaoTemplateProperties.class
})
public class ConfigurationPropertiesConfig {
}
