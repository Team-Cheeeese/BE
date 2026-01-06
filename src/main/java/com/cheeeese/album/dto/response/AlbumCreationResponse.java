package com.cheeeese.album.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@Schema(
        description = "앨범 생성 응답",
        requiredProperties = {
                "themeEmoji",
                "title",
                "eventDate",
                "createdAt",
                "isFirst",
                "currentPhotoCnt",
                "code"
        }
)public record AlbumCreationResponse(
        @Schema(description = "앨범 테마 이모지", example = "U+1F9C0")
        String themeEmoji,

        @Schema(description = "행사 이름", example = "큐시즘 MT")
        String title,

        @Schema(description = "행사 날짜", example = "2025.02.01")
        LocalDate eventDate,

        @Schema(description = "앨범 생성일시", example = "2026-01-05T17:23:16.201417")
        LocalDateTime createdAt,

        @Schema(description = "해당 사용자가 최초로 생성한 앨범인지 여부", example = "true")
        boolean isFirst,

        @Schema(description = "현재까지 업로드된 사진 수", example = "1")
        int currentPhotoCnt,

        @Schema(description = "앨범 코드", example = "786ccd09-5f22-4aa9-a32b-f62dd2e94cc8")
        String code
) {
}
