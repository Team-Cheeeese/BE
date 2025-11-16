package com.cheeeese.album.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(
        description = "앨범 업로드 가능 사진 수 응답 DTO",
        requiredProperties = {
                "availableCount",
                "maxPhotoCount",
                "currentPhotoCount"
        }
)
public record UploadAvailableCountResponse(
        @Schema(description = "현재 앨범에 업로드 가능한 최대 사진 수", example = "850")
        int availableCount,

        @Schema(description = "앨범의 최대 사진 개수", example = "1000")
        int maxPhotoCount,

        @Schema(description = "현재 업로드된 사진 수 (UPLOADING, PROCESSING, COMPLETED 포함)", example = "150")
        int currentPhotoCount
) {
}
