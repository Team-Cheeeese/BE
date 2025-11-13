package com.cheeeese.photo.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "베스트 앨범컷 조회 API")
public record PhotoBest4CutResponse(
        @Schema(description = "썸네일 이미지 url", example = "https://cdn.say-cheese.me/...")
        String thumbnailUrl,

        @Schema(description = "좋아요 수", example = "1")
        int likeCnt,

        @Schema(description = "좋아요 여부", example = "true")
        boolean isLiked
) {
}
