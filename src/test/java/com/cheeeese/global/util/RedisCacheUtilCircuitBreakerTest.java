package com.cheeeese.global.util;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.RedisTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@SpringBootTest
public class RedisCacheUtilCircuitBreakerTest {

    @Autowired
    private RedisCacheUtil redisCacheUtil;

    @Autowired
    private CircuitBreakerRegistry circuitBreakerRegistry;

    @MockitoBean(name = "cacheRedisTemplate")
    private RedisTemplate<String, Object> cacheRedisTemplate;

    private CircuitBreaker circuitBreaker;

    @BeforeEach
    void setUp() {
        circuitBreaker = circuitBreakerRegistry.circuitBreaker("redisCache");
        circuitBreaker.reset(); // 매 테스트마다 CLOSED로 초기화
    }

    @Test
    @DisplayName("Redis 조회 실패 시 fallback으로 null을 반환한다")
    void getValue_fallbackOnFailure() {
        // given
        given(cacheRedisTemplate.opsForValue())
                .willThrow(new RedisConnectionFailureException("Unable to connect to Redis"));

        // when
        Long result = redisCacheUtil.getValue("test-key");

        // then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("실패가 임계치(실패율 50%, 최소 10건)를 넘으면 서킷이 OPEN으로 전환된다")
    void circuitBreaker_opensAfterThreshold() {
        // given
        given(cacheRedisTemplate.opsForValue())
                .willThrow(new RedisConnectionFailureException("Unable to connect to Redis"));

        // when — minimum-number-of-calls(10) 이상 호출
        for (int i = 0; i < 10; i++) {
            redisCacheUtil.getValue("test-key-" + i);
        }

        // then
        assertThat(circuitBreaker.getState()).isEqualTo(CircuitBreaker.State.OPEN);
    }

    @Test
    @DisplayName("서킷이 OPEN 상태면 Redis를 호출하지 않고 즉시 fallback을 반환한다")
    void circuitOpen_skipsRedisCall() {
        // given
        circuitBreaker.transitionToOpenState();

        // when
        Long result = redisCacheUtil.getValue("test-key");

        // then — opsForValue() 자체가 호출 안 됐는지 검증
        verify(cacheRedisTemplate, never()).opsForValue();
        assertThat(result).isNull();
    }
}
