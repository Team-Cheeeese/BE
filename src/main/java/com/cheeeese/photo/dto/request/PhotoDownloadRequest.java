package com.cheeeese.photo.dto.request;

import lombok.Builder;

import java.util.List;

@Builder
public record PhotoDownloadRequest(
        String code,
        List<Long> photoIds
) {
}
