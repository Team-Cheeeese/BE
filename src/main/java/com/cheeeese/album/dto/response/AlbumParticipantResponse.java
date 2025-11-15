package com.cheeeese.album.dto.response;

import com.cheeeese.album.domain.type.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Builder
@Schema(
        description = "앨범 참여자 목록 응답 DTO (활성/만료 공용)",
        requiredProperties = {
                "isExpired",
                "title",
                "themeEmoji",
                "eventDate",
                "expiredAt",
                "maxParticipantCount",
                "currentParticipantCount",
                "participants"
        }
)
public record AlbumParticipantResponse(

        @Schema(description = "만료 여부", example = "false")
        boolean isExpired,

        @Schema(description = "앨범 제목", example = "졸업식")
        String title,

        @Schema(description = "앨범 테마 이모지 (또는 이미지 URL)", example = "U+1F9C0")
        String themeEmoji,

        @Schema(description = "이벤트 날짜 (yyyy-MM-dd)", example = "2025-02-01")
        LocalDate eventDate,

        @Schema(description = "만료 일시 (ISO 형식)", example = "2025-10-30T12:11:27.282")
        LocalDateTime expiredAt,

        @Schema(description = "총 참여 가능 인원", example = "64")
        Integer maxParticipantCount,

        @Schema(description = "현재 앨범 참가 인원", example = "58")
        Integer currentParticipantCount,

        @Schema(description = "참가자 목록 (정렬 포함)")
        List<AlbumParticipantListResponse.ParticipantInfo> participants,

        @Schema(description = "현재 사용자의 역할 (MAKER / GUEST)", example = "GUEST", nullable = true)
        Role myRole
) {}
