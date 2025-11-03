package com.cheeeese.photo.dto.response;

import lombok.Builder;

@Builder
public record PhotoDetailResponse(
        String name,
        Long photoId,
        String imageUrl,
        String thumbnailUrl,
        int likesCnt,
        boolean isLiked,
        boolean isDownloaded
) {
}
