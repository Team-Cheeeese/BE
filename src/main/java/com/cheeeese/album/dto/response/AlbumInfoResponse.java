package com.cheeeese.album.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDate;

@Builder
@Schema(description = "앨범 정보 API")
public record AlbumInfoResponse(
        Long makerId,
        String title,
        String themeEmoji,
        int participant,
        int currentParticipant,
        LocalDate eventDate,
        int currentPhotoCnt
) {
}
