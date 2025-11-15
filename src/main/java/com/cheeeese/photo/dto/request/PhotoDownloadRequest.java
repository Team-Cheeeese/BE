package com.cheeeese.photo.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;

import java.util.List;

@Builder
public record PhotoDownloadRequest(
        @Schema(description = "앨범 코드", example = "1f0b7ea8-fab6-6581-95e3-0720bc07603e")
        @NotBlank(message = "앨범 코드는 필수입니다")
        String code,

        @Schema(description = "사진 고유 ID", example = "[1, 2, 3]")
        @NotEmpty(message = "사진 ID 목록은 비어있을 수 없습니다")
        List<Long> photoIds
) {
}
