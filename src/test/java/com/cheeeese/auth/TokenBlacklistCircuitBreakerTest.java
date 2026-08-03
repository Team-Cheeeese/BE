package com.cheeeese.auth;

import com.cheeeese.auth.application.TokenBlacklistService;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

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

    @Test
    @DisplayName("Redis 예외 발생 시 블랙리스트 등록 실패를 전파한다")
    void addBlackList_propagatesRedisFailure() {
        given(tokenRedisTemplate.opsForValue())
                .willThrow(new RedisConnectionFailureException("Redis unavailable"));

        assertThatThrownBy(() -> tokenBlacklistService.addBlackList(
                "access-token", "logout", java.time.Duration.ofMinutes(5)))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("Redis 예외 발생 시 블랙리스트 조회를 fail-closed 처리한다")
    void isBlackListed_failsClosedOnRedisFailure() {
        given(tokenRedisTemplate.hasKey("accessTokenBlackList:access-token"))
                .willThrow(new RedisConnectionFailureException("Redis unavailable"));

        assertThat(tokenBlacklistService.isBlackListed("access-token")).isTrue();
    }

    @Test
    @DisplayName("서킷 OPEN 상태에서 Redis를 호출하지 않고 fail-closed 처리한다")
    void isBlackListed_failsClosedWhenCircuitIsOpen() {
        circuitBreaker.transitionToOpenState();

        assertThat(tokenBlacklistService.isBlackListed("access-token")).isTrue();
        verify(tokenRedisTemplate, never()).hasKey("accessTokenBlackList:access-token");
    }
}
