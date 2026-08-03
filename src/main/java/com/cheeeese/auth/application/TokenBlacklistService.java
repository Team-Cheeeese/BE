package com.cheeeese.auth.application;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
@Service
public class TokenBlacklistService {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final String BLACKLIST_PREFIX = "accessTokenBlackList:";

    public TokenBlacklistService(
            @Qualifier("tokenRedisTemplate") RedisTemplate<String, Object> redisTemplate
    ) {
        this.redisTemplate = redisTemplate;
    }

    @CircuitBreaker(name = "tokenBlacklist", fallbackMethod = "addBlackListFallback")
    public void addBlackList(String token, Object o, Duration expiration) {
        String key = BLACKLIST_PREFIX + token;
        redisTemplate.opsForValue().set(key, o, expiration);
    }

    private void addBlackListFallback(String token, Object o, Duration expiration, Throwable t) {
        log.error("[Redis][CircuitBreaker] 블랙리스트 등록 실패. cause={}", t.toString());
        throw new IllegalStateException("Token blacklist registration failed", t);
    }

    @CircuitBreaker(name = "tokenBlacklist", fallbackMethod = "isBlackListedFallback")
    public boolean isBlackListed(String token) {
        String key = BLACKLIST_PREFIX + token;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    private boolean isBlackListedFallback(String token, Throwable t) {
        log.error("[Redis][CircuitBreaker] 블랙리스트 조회 실패 - 인증을 차단합니다. cause={}", t.toString());
        return true;
    }
}
