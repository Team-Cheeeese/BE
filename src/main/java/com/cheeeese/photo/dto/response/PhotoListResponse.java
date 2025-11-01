package com.cheeeese.photo.dto.response;

import lombok.Builder;

@Builder
public record PhotoListResponse(
        Long photoId,
        String thumbnailUrl
) {
}
