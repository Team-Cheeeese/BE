package com.cheeeese.album.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDate;

@Builder
@Schema(
        description = "앨범 생성 요청",
        requiredProperties = {
                "themeEmoji",
                "title",
                "participant",
                "eventDate"
        }
)
public record AlbumCreationRequest(
        @Schema(description = "앨범 테마 이모지", example = "U+1F9C0")
        String themeEmoji,

        @Schema(description = "앨범 이름", example = "졸업식")
        String title,

        @Schema(description = "참여자 수", example = "64")
        int participant,

        @Schema(description = "행사 날짜", example = "2025-02-01")
        LocalDate eventDate
) {
}
