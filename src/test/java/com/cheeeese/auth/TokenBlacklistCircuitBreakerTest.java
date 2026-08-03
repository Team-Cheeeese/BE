package com.cheeeese.auth;

import com.cheeeese.auth.application.TokenBlacklistService;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class TokenBlacklistCircuitBreakerTest {

    @Autowired
    private TokenBlacklistService tokenBlacklistService;

    @Autowired
    private CircuitBreakerRegistry circuitBreakerRegistry;

    @MockitoBean(name = "tokenRedisTemplate")
    private RedisTemplate<String, Object> tokenRedisTemplate;

    private CircuitBreaker circuitBreaker;

    @BeforeEach
    void setUp() {
        circuitBreaker = circuitBreakerRegistry.circuitBreaker("tokenBlacklist");
        circuitBreaker.reset();
    }

    @Test
    @DisplayName("redisCache 서킷과 독립적으로 동작한다")
    void independentFromRedisCacheCircuit() {
        CircuitBreaker redisCacheCircuit = circuitBreakerRegistry.circuitBreaker("redisCache");
        redisCacheCircuit.transitionToOpenState(); // redisCache만 OPEN으로

        // tokenBlacklist는 여전히 CLOSED여야 함
        assertThat(circuitBreaker.getState()).isEqualTo(CircuitBreaker.State.CLOSED);
    }
}
