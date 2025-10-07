package com.cheeeese.auth.infrastructure.mapper;

import com.cheeeese.auth.domain.RefreshToken;
import com.cheeeese.user.domain.User;

public class RefreshTokenMapper {

    public static RefreshToken toRefreshToken(User user, String refreshToken) {
        return RefreshToken.builder()
                .userId(user.getId())
                .refreshToken(refreshToken)
                .build();
    }
}
