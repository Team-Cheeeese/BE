package com.cheeeese.auth.infrastructure.mapper;

import com.cheeeese.auth.dto.response.TempCodeExchangeResponse;
import com.cheeeese.user.domain.User;

public class AuthMapper {

    public static TempCodeExchangeResponse toResponse(String accessToken, String refreshToken, User user) {
        return TempCodeExchangeResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }
}
