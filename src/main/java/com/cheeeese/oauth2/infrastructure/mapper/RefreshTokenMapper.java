package com.cheeeese.oauth2.infrastructure.mapper;

import com.cheeeese.oauth2.domain.RefreshToken;
import com.cheeeese.user.domain.User;

public class RefreshTokenMapper {

    public static RefreshToken toRefreshToken(User user, String refreshToken, Long expiration) {
        return RefreshToken.builder()
                .userId(user.getId())
                .refreshToken(refreshToken)
                .expiration(expiration)
                .build();
    }
}
