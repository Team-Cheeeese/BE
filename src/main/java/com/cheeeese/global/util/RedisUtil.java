package com.cheeeese.global.util;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedisUtil {

    @Qualifier("tokenRedisTemplate")
    private final RedisTemplate<String, Object> tokenRedisTemplate;

    public void setValue(String key, Object value, Long expiredTime) {
        tokenRedisTemplate.opsForValue().set(key, value, expiredTime, TimeUnit.MILLISECONDS);
    }

    public String getValue(String key) {
        return (String) tokenRedisTemplate.opsForValue().get(key);
    }

    public void deleteValue(String key) {
        tokenRedisTemplate.delete(key);
    }
}
