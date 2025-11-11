package com.cheeeese.cheese4cut.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.util.List;

@Builder
@Schema(description = "치즈네컷 수동 확정 요청 DTO")
public record Cheese4cutFixedRequest(
        @NotEmpty
        @Size(min = 4, max = 4, message = "4장의 사진 ID를 선택해야 합니다.")
        @Schema(description = "선택된 사진 ID 목록 (정확히 4개)", example = "[101, 105, 122, 140]")
        List<Long> photoIds
) {
}