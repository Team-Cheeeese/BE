package com.cheeeese.global.util;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedisCacheUtil {

    @Qualifier("cacheRedisTemplate")
    private final RedisTemplate<String, Object> cacheRedisTemplate;

    /**
     * 캐시 저장
     */
    public void setValue(String key, Object value, Long expiredTime) {
        if (expiredTime != null) {
            cacheRedisTemplate.opsForValue().set(key, value, expiredTime, TimeUnit.SECONDS);
        } else {
            cacheRedisTemplate.opsForValue().set(key, value);
        }
    }

    public Long getValue(String key) {
        return (Long) cacheRedisTemplate.opsForValue().get(key);
    }

    /**
     * 캐시 객체 조회
     */
    @SuppressWarnings("unchecked")
    public <T> T getObject(String key, Class<T> clazz) {
        return (T) cacheRedisTemplate.opsForValue().get(key);
    }

    /**
     * 패턴 기반 키 삭제 (대규모 삭제)
     */
    public void deletePattern(String pattern) {
        Set<String> keys = cacheRedisTemplate.keys(pattern);
        cacheRedisTemplate.delete(keys);
    }

    public boolean exists(String key) {
        return cacheRedisTemplate.hasKey(key);
    }
}
