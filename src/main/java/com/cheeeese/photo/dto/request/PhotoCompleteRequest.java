package com.cheeeese.photo.dto.request;


import jakarta.validation.constraints.NotNull;

public record PhotoCompleteRequest(
        @NotNull(message = "photoId는 필수입니다")
        Long photoId,

        @NotNull(message = "thumbnailUrl은 필수입니다")
        String thumbnailUrl
) {}
