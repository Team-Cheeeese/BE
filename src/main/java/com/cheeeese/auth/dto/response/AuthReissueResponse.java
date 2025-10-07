package com.cheeeese.auth.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "token 재발급")
public record AuthReissueResponse(
        @Schema(
                description = "새로 발급된 accessToken",
                example = "eyJh.eqi57hK"
        )
        String accessToken,

        @Schema(
                description = "새로 발급된 refreshToken",
                example = "eyJh.eqi57hK"
        )
        String refreshToken
) {
}
