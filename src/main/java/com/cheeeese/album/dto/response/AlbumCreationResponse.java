package com.cheeeese.album.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDate;

@Builder
@Schema(description = "앨범 생성 API")
public record AlbumCreationResponse(
        @Schema(description = "썸네일 이미지", example = "https://theme.jpg")
        String themeImageUrl,

        @Schema(description = "행사 날짜", example = "2025.02.01")
        LocalDate eventDate,

        @Schema(description = "현재 참여자 수", example = "1")
        int currentParticipant,

        @Schema(description = "앨범 코드", example = "786ccd09-5f22-4aa9-a32b-f62dd2e94cc8")
        String code
) {
}
