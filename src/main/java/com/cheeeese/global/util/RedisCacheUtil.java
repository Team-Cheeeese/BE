package com.cheeeese.global.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisCacheUtil {

    @Qualifier("cacheRedisTemplate")
    private final RedisTemplate<String, Object> cacheRedisTemplate;

    /**
     * 캐시 저장
     */
    public void setValue(String key, Object value, Long expiredTime) {
        try {
            if (expiredTime != null) {
                cacheRedisTemplate.opsForValue().set(key, value, expiredTime, TimeUnit.SECONDS);
            } else {
                cacheRedisTemplate.opsForValue().set(key, value);
            }

        } catch (DataAccessException e) {
            log.warn("[Redis] Cache unavailable. Skip cache write. key={}", key);
        }
    }

    /**
     * Long 조회
     */
    public Long getValue(String key) {
        try {
            Object value = cacheRedisTemplate.opsForValue().get(key);

            if (value instanceof Long) return (Long) value;
            if (value instanceof Integer) return ((Integer) value).longValue();

            return null;
        } catch (DataAccessException e) {
            log.warn("[Redis] Cache unavailable. Fallback to DB. key={}", key);
            return null;
        }
    }

    /**
     * 객체 조회
     */
    @SuppressWarnings("unchecked")
    public <T> T getObject(String key, Class<T> clazz) {
        try {
            return (T) cacheRedisTemplate.opsForValue().get(key);
        } catch (DataAccessException e) {
            log.warn("[Redis] Cache unavailable. Fallback to DB. key={}", key);
            return null;
        }
    }

    /**
     * 패턴 삭제
     */
    public void deletePattern(String pattern) {
        try {
            Set<String> keys = cacheRedisTemplate.keys(pattern);

            if (keys != null && !keys.isEmpty()) {
                cacheRedisTemplate.delete(keys);
            }

        } catch (DataAccessException e) {
            log.warn("[Redis] Cache delete skipped. pattern={}", pattern);
        }
    }

    public boolean exists(String key) {
        try {
            return Boolean.TRUE.equals(cacheRedisTemplate.hasKey(key));
        } catch ( DataAccessException e) {
            log.warn("[Redis] Cache exists check failed. key={}", key);
            return false;
        }
    }
}
