package com.cheeeese.auth.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record AuthExchangeResponse(
        @Schema(
                description = "accessToken",
                example = "eyJh.eqi57hK"
        )
        String accessToken,

        @Schema(
                description = "refreshToken",
                example = "eyJh.eqi57hK"
        )
        String refreshToken,

        @Schema(
                description = "사용자 고유 식별 ID",
                example = "1"
        )
        Long userId,

        @Schema(
                description = "사용자 이름",
                example = "주정빈"
        )
        String name,

        @Schema(
                description = "사용자 이메일",
                example = "yui2507@naver.com"
        )
        String email
) {
}
