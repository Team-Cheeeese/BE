package com.cheeeese.photo.dto.response;

import lombok.Builder;

@Builder
public record PhotoBest4CutResponse(
        String thumbnailUrl,
        int likeCnt,
        boolean isLiked
) {
}
