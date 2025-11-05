package com.cheeeese.cheese4cut.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "치즈네컷 조회 응답 (다형 구조)")
public sealed interface Cheese4cutResponse
        permits Cheese4cutPreviewResponse, Cheese4cutFinalResponse {
        boolean isFinalized();
}
