package com.cheeeese.global.config;

import com.cheeeese.global.config.properties.CorsProperties;
import com.cheeeese.global.security.jwt.JwtProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(value = {
        JwtProperties.class,
        CorsProperties.class
})
public class ConfigurationPropertiesConfig {
}
