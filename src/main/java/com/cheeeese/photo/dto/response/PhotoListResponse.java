package com.cheeeese.photo.dto.response;

import lombok.Builder;

@Builder(toBuilder = true)
public record PhotoListResponse(
        Long photoId,
        String thumbnailUrl,
        int likeCnt,
        boolean isLiked,
        boolean isDownloaded
) {
    public PhotoListResponse withUserStatus(boolean isLiked, boolean isDownloaded) {
        return this.toBuilder()
                .isLiked(isLiked)
                .isDownloaded(isDownloaded)
                .build();
    }
}
