package com.cheeeese.photo.dto.response;

import lombok.Builder;

@Builder
public record PhotoLikedResponse(
        Long photoId,
        String thumbnailUrl,
        boolean isDownloaded
) {
}
