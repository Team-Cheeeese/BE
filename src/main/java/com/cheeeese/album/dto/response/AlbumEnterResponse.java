package com.cheeeese.album.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Schema(description = "앨범 입장 응답 DTO")
public record AlbumEnterResponse(
        @Schema(description = "앨범 제목", example = "졸업 여행 폴라로이드")
        String title,

        @Schema(description = "앨범 테마 이모지", example = "U+1F9C0")
        String themeEmoji,

        @Schema(description = "이벤트 날짜", example = "2025-02-26")
        String eventDate,

        @Schema(description = "열람 종료 시각 (만료일)", example = "2025-03-05T00:00:00")
        LocalDateTime expiredAt,

        @Schema(description = "총 참여 가능 인원", example = "64")
        Integer maxParticipantCount,

        @Schema(description = "현재 앨범 참가 인원", example = "58")
        Integer currentParticipantCount,

        @Schema(description = "앨범 내 총 사진 개수", example = "150")
        Long totalPhotoCount,

        @Schema(description = "앨범 최대 사진 개수", example = "1000")
        Integer maxPhotoCount,

        @Schema(description = "호스트 정보")
        AlbumHostInfo hostInfo,

        @Schema(description = "참가자 목록 (호스트 포함)")
        List<AlbumParticipantResponse> participants,

        @Schema(description = "최근 사진 미리보기 URL 목록 (최대 9개 등)")
        List<String> recentPhotoUrls
) {
    @Builder
    @Schema(description = "앨범 호스트 정보")
    public record AlbumHostInfo(
            @Schema(description = "호스트 이름", example = "주정빈")
            String hostName,

            @Schema(description = "호스트 프로필 이미지 URL (이모지 포함)", example = "http://example.com/profile.png")
            String hostProfileImage
    ) {
    }

    @Builder
    @Schema(description = "앨범 참가자 정보")
    public record AlbumParticipantResponse(
            @Schema(description = "사용자 이름", example = "주정빈")
            String name,

            @Schema(description = "사용자 프로필 이미지 URL (이모지 포함)", example = "http://example.com/profile.png")
            String profileImage
    ) {
    }
}