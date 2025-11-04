package com.cheeeese.photo.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "사진 상세 조회 API")
public record PhotoDetailResponse(
        @Schema(description = "사용자 이름", example = "주정빈")
        String name,

        @Schema(description = "사진 ID", example = "1")
        Long photoId,

        @Schema(description = "사진 원본 url", example = "example.jpg")
        String imageUrl,

        @Schema(description = "사진 썸네일 url", example = "example.jpg")
        String thumbnailUrl,

        @Schema(description = "좋아요 수", example = "1")
        int likesCnt,

        @Schema(description = "좋아요 여부", example = "true")
        boolean isLiked,

        @Schema(description = "다운로드 여부", example = "false")
        boolean isDownloaded,

        @Schema(description = "1시간 이내 다운로드 여부", example = "false")
        boolean isRecentlyDownloaded
) {
}
