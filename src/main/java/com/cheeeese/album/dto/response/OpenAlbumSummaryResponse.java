package com.cheeeese.album.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Builder
@Schema(description = "열린 앨범 요약 정보")
public record OpenAlbumSummaryResponse(
        @Schema(description = "앨범 코드", example = "1f0ba577-39f3-69b6-abab-455897f404fe")
        String code,

        @Schema(description = "앨범 테마 이모지", example = "U+1F36F")
        String themeEmoji,

        @Schema(description = "앨범 제목", example = "봄 소풍")
        String title,

        @Schema(description = "이벤트 날짜", example = "2025-05-01")
        LocalDate eventDate,

        @Schema(description = "앨범 생성자 이름", example = "치즈메이커")
        String makerName,

        @Schema(description = "현재 참여자 수", example = "8")
        int currentParticipant,

        @Schema(description = "전체 참여자 수", example = "10")
        int participant,

        @Schema(description = "앨범 만료 예정 일시", example = "2025-05-05T12:00:00")
        LocalDateTime expiredAt,

        @JsonInclude(JsonInclude.Include.ALWAYS)
        @Schema(description = "최근 업로드된 사진 썸네일 3장")
        List<String> recentPhotoThumbnails
) {
}
