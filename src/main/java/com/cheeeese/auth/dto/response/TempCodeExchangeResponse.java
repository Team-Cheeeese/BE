package com.cheeeese.auth.dto.response;

import lombok.Builder;

@Builder
public record TempCodeExchangeResponse(
        String accessToken,
        String refreshToken,
        Long userId,
        String name,
        String email
) {
}
