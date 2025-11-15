package com.cheeeese.photo.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder(toBuilder = true)
@Schema(
        description = "사진 목록 조회 API",
        requiredProperties = {
                "photoId",
                "thumbnailUrl",
                "likeCnt",
                "isLiked",
                "isDownloaded",
                "isRecentlyDownloaded"
        }
)
public record PhotoListResponse(
        @Schema(description = "사진 ID", example = "1")
        Long photoId,

        @Schema(description = "사진 썸네일 url", example = "example.jpg")
        String thumbnailUrl,

        @Schema(description = "좋아요 수", example = "1")
        int likeCnt,

        @Schema(description = "좋아요 여부", example = "true")
        boolean isLiked,

        @Schema(description = "다운로드 여부", example = "false")
        boolean isDownloaded,

        @Schema(description = "1시간 이내 다운로드 여부", example = "false")
        boolean isRecentlyDownloaded
) {
    public PhotoListResponse withUserStatus(boolean isLiked, boolean isDownloaded, boolean isRecentlyDownloaded) {
        return this.toBuilder()
                .isLiked(isLiked)
                .isDownloaded(isDownloaded)
                .isRecentlyDownloaded(isRecentlyDownloaded)
                .build();
    }
}
