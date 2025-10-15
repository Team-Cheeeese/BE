package com.cheeeese.album.dto.request;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record AlbumCreateRequest(
        String themeImageUrl,
        String title,
        int participant,
        LocalDate eventData,
        boolean isTermsAgreement
) {
}
