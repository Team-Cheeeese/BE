package com.cheeeese.photo.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record PhotoPageResponse(
        List<PhotoListResponse> responses,
        int listSize,
        boolean isFirst,
        boolean isLast,
        boolean hasNext
) {
}
