package com.cheeeese.photo.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder(toBuilder = true)
@Schema(
        description = "사진 목록 조회 API",
        requiredProperties = {
                "photoId",
                "profileImage",
                "thumbnailUrl",
                "likeCnt",
                "isLiked",
                "isDownloaded",
                "isRecentlyDownloaded",
                "canDelete"
        }
)
public record PhotoListResponse(
        @Schema(description = "사진 업로더 이름", example = "주정빈")
        String name,

        @Schema(description = "사진 ID", example = "1")
        Long photoId,

        @Schema(description = "업로더 프로필 이미지", example = "example.jpg")
        String profileImage,

        @Schema(description = "사진 원본 url", example = "example.jpg")
        String imageUrl,

        @Schema(description = "사진 썸네일 url", example = "example.jpg")
        String thumbnailUrl,

        @Schema(description = "좋아요 수", example = "1")
        int likeCnt,

        @Schema(description = "좋아요 여부", example = "true")
        boolean isLiked,

        @Schema(description = "다운로드 여부", example = "false")
        boolean isDownloaded,

        @Schema(description = "1시간 이내 다운로드 여부", example = "false")
        boolean isRecentlyDownloaded,

        @Schema(description = "삭제 가능 여부", example = "true")
        boolean canDelete
) {
    public PhotoListResponse withUserStatus(
            boolean isLiked,
            boolean isDownloaded,
            boolean isRecentlyDownloaded,
            boolean canDelete
    ) {
        return this.toBuilder()
                .isLiked(isLiked)
                .isDownloaded(isDownloaded)
                .isRecentlyDownloaded(isRecentlyDownloaded)
                .canDelete(canDelete)
                .build();
    }
}
