package com.cheeeese.photo.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record PhotoInfoResponse(
        String name,
        LocalDateTime captureTime,
        LocalDateTime createdAt
) {
}
