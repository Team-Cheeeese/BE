package com.cheeeese.cheese4cut.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "치즈네컷 확정 완료 응답 DTO (최종 프레임 이미지)")
public record Cheese4cutFinalResponse(
        @Schema(description = "확정 여부 (항상 true)", example = "true")
        boolean isFinalized
) implements Cheese4cutResponse {
}
