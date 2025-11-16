package com.cheeeese.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(
        description = "token 재발급",
        requiredProperties = {
                "refreshToken"
        }
)
public record AuthReissueRequest(
        @Schema(
                description = "유효한 refreshToken",
                example = "eyJh.eqi57hK"
        )
        String refreshToken
) {
}
