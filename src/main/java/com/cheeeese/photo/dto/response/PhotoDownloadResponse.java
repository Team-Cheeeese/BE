package com.cheeeese.photo.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record PhotoDownloadResponse(
        List<DownloadFileInfo> downloadFiles
) {
    @Builder
    public record DownloadFileInfo(
            Long photoId,
            String downloadUrl,
            String fileName,
            LocalDateTime captureTime,
            LocalDateTime createdAt
    ) {}
}
