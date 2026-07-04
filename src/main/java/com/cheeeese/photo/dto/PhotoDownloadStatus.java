package com.cheeeese.photo.dto;

import java.time.LocalDateTime;

public record PhotoDownloadStatus(
        Long photoId,
        LocalDateTime updatedAt
) {
}
