package com.cheeeese.global.util;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
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

    @CircuitBreaker(name = "redisCache", fallbackMethod = "setValueFallback")
    public void setValue(String key, Object value, Long expiredTime) {
        if (expiredTime != null) {
            cacheRedisTemplate.opsForValue().set(key, value, expiredTime, TimeUnit.SECONDS);
        } else {
            cacheRedisTemplate.opsForValue().set(key, value);
        }
    }

    private void setValueFallback(String key, Object value, Long expiredTime, Throwable t) {
        log.warn("[Redis][CircuitBreaker] Cache unavailable. Skip cache write. key={}, cause={}", key, t.toString());
    }

    @CircuitBreaker(name = "redisCache", fallbackMethod = "getValueFallback")
    public Long getValue(String key) {
        Object value = cacheRedisTemplate.opsForValue().get(key);
        if (value instanceof Long) return (Long) value;
        if (value instanceof Integer) return ((Integer) value).longValue();
        return null;
    }

    private Long getValueFallback(String key, Throwable t) {
        log.warn("[Redis][CircuitBreaker] Cache unavailable. Fallback to DB. key={}, cause={}", key, t.toString());
        return null;
    }

    @SuppressWarnings("unchecked")
    @CircuitBreaker(name = "redisCache", fallbackMethod = "getObjectFallback")
    public <T> T getObject(String key, Class<T> clazz) {
        return (T) cacheRedisTemplate.opsForValue().get(key);
    }

    private <T> T getObjectFallback(String key, Class<T> clazz, Throwable t) {
        log.warn("[Redis][CircuitBreaker] Cache unavailable. Fallback to DB. key={}, cause={}", key, t.toString());
        return null;
    }

    @CircuitBreaker(name = "redisCache", fallbackMethod = "deletePatternFallback")
    public void deletePattern(String pattern) {
        Set<String> keys = cacheRedisTemplate.keys(pattern);
        if (keys != null && !keys.isEmpty()) {
            cacheRedisTemplate.delete(keys);
        }
    }

    private void deletePatternFallback(String pattern, Throwable t) {
        log.warn("[Redis][CircuitBreaker] Cache delete skipped. pattern={}, cause={}", pattern, t.toString());
    }

    @CircuitBreaker(name = "redisCache")
    public void incrementStrict(String key) {
        cacheRedisTemplate.opsForValue().increment(key);
    }

    @CircuitBreaker(name = "redisCache")
    public void deletePatternStrict(String pattern) {
        Set<String> keys = cacheRedisTemplate.keys(pattern);
        if (keys != null && !keys.isEmpty()) {
            cacheRedisTemplate.delete(keys);
        }
    }

    @CircuitBreaker(name = "redisCache", fallbackMethod = "existsFallback")
    public boolean exists(String key) {
        return Boolean.TRUE.equals(cacheRedisTemplate.hasKey(key));
    }

    private boolean existsFallback(String key, Throwable t) {
        log.warn("[Redis][CircuitBreaker] Cache exists check failed. key={}, cause={}", key, t.toString());
        return false;
    }
}
