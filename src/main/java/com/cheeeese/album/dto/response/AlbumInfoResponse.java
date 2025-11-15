package com.cheeeese.album.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@Schema(description = "앨범 정보 API")
public record AlbumInfoResponse(
        @Schema(description = "생성자 ID", example = "1")
        Long makerId,

        @Schema(description = "앨범 제목", example = "김수한무거북이")
        String title,

        @Schema(description = "테마 이모지", example = "U+1F9C0")
        String themeEmoji,

        @Schema(description = "참여 가능 인원 수", example = "64")
        int participant,

        @Schema(description = "현재 참여자 수", example = "30")
        int currentParticipant,

        @Schema(description = "이벤트 날짜", example = "2025-02-01")
        LocalDate eventDate,

        @Schema(description = "현재 사진 수", example = "1212")
        int currentPhotoCnt,

        @Schema(description = "앨범 만료일자", example = "2025-11-17 16:53:35.430336")
        LocalDateTime expiredAt
) {
}
