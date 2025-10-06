package com.cheeeese.auth.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.util.concurrent.TimeUnit;

@Getter
@NoArgsConstructor
@RedisHash(value = "refreshToken")
public class RefreshToken {

    @Id
    private Long userId;

    private String refreshToken;

    @TimeToLive(unit = TimeUnit.MILLISECONDS)
    private Long expiration;

    @Builder
    private RefreshToken(Long userId, String refreshToken, Long expiration) {
        this.userId = userId;
        this.refreshToken = refreshToken;
        this.expiration = expiration;
    }

    public void updateRefreshToken(String refreshToken, Long expiration) {
        this.refreshToken = refreshToken;
        this.expiration = expiration;
    }
}
