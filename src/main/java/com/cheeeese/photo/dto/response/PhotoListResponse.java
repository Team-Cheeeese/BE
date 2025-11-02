package com.cheeeese.photo.dto.response;

import lombok.Builder;

@Builder
public record PhotoListResponse(
        Long photoId,
        String thumbnailUrl,
        int likeCnt,
        boolean isLiked,
        boolean isDownloaded
) {
}
