package com.cheeeese.auth.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "token 재발급")
public record AuthReissueResponse(
        String accessToken,
        String refreshToken
) {
}
