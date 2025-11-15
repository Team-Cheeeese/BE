package com.cheeeese.photo.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(
        description = "내가 띱한 사진 목록 조회 API",
        requiredProperties = {
                "photoId",
                "thumbnailUrl",
                "isDownloaded",
                "isRecentlyDownloaded"
        }
)
public record PhotoLikedResponse(
        @Schema(description = "사진 ID", example = "1")
        Long photoId,

        @Schema(description = "사진 썸네일 url", example = "example.jpg")
        String thumbnailUrl,

        @Schema(description = "다운로드 여부", example = "false")
        boolean isDownloaded,

        @Schema(description = "1시간 이내 다운로드 여부", example = "false")
        boolean isRecentlyDownloaded
) {
}
