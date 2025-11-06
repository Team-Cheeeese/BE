package com.cheeeese.photo.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record PhotoDownloadResponse(
        String fileName,
        LocalDateTime captureTime,
        LocalDateTime createdAt
) {
}
