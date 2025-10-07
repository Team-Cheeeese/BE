package com.cheeeese.auth.infrastructure.mapper;

import com.cheeeese.auth.dto.response.AuthReissueResponse;
import com.cheeeese.auth.dto.response.AuthExchangeResponse;
import com.cheeeese.user.domain.User;

public class AuthMapper {

    public static AuthExchangeResponse toExchangeResponse(String accessToken, String refreshToken, User user) {
        return AuthExchangeResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public static AuthReissueResponse toReissueResponse(String accessToken, String refreshToken) {
        return AuthReissueResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
