package com.cheeeese.album.dto.response;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record AlbumCreateResponse(
        String themeImageUrl,
        LocalDate eventDate,
        int currentParticipant,
        String code
) {
}
