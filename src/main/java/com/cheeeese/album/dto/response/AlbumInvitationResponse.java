package com.cheeeese.album.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
@Schema(description = "앨범 초대장 확인 응답 DTO")
public record AlbumInvitationResponse(
        @Schema(description = "앨범 제목", example = "경영학부 졸업식")
        String title,

        @Schema(description = "앨범 테마 이모지", example = "U+1F9C0")
        String themeEmoji,

        @Schema(description = "이벤트 날짜", example = "2025-02-26")
        String eventDate,

        @Schema(description = "열람 종료 시각 (만료일)", example = "2025-03-05T00:00:00")
        LocalDateTime expiredAt,

        @Schema(description = "호스트 이름", example = "이유")
        String hostName,

        @Schema(description = "호스트 프로필 이미지 URL", example = "http://example.com/host_profile.png")
        String hostProfileImage
) {
}