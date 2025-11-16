package com.cheeeese.album.dto.response;

import com.cheeeese.album.domain.type.AlbumJoinStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;

@Schema(
        description = "앨범 재입장 또는 기존 참여자 응답 DTO",
        requiredProperties = {
                "joinStatus",
                "title",
                "themeEmoji",
                "eventDate",
                "expiredAt",
                "makerInfo"
        }
)
@Builder
public record ExistingEnterResponse(
        @Schema(description = "참여 상태 (EXISTING | REJOINED)", example = "EXISTING")
        AlbumJoinStatus joinStatus,

        @Schema(description = "앨범 제목", example = "우리 여행 앨범")
        String title,

        @Schema(description = "앨범 테마 이모지", example = "📸")
        String themeEmoji,

        @Schema(description = "이벤트 날짜 (YYYY-MM-DD 형식 문자열)", example = "2025-10-31")
        String eventDate,

        @Schema(description = "앨범 만료 시각", example = "2025-11-30T23:59:59")
        LocalDateTime expiredAt,

        @Schema(description = "앨범 생성자(호스트) 정보")
        AlbumMakerInfo makerInfo

) implements AlbumEnterResponse {}
