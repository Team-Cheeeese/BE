package com.cheeeese.album.dto.response;

import com.cheeeese.album.domain.type.AlbumJoinStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "앨범 첫 입장(신규 참여자) 응답 DTO")
@Builder
public record NewEnterResponse(
        @Schema(description = "참여 상태 (항상 NEW)", example = "NEW")
        AlbumJoinStatus joinStatus,

        @Schema(description = "앨범 제목", example = "여름 바캉스")
        String title,

        @Schema(description = "앨범 테마 이모지", example = "🌴")
        String themeEmoji,

        @Schema(description = "이벤트 날짜 (YYYY-MM-DD 형식 문자열)", example = "2025-08-14")
        String eventDate,

        @Schema(description = "앨범 만료 시각", example = "2025-09-01T00:00:00")
        LocalDateTime expiredAt,

        @Schema(description = "앨범 생성자 정보")
        AlbumMakerInfo makerInfo,

        @Schema(description = "남은 업로드 가능 사진 수", example = "5")
        Integer remainingUploadSlots,

        @Schema(description = "최근 업로드된 사진 썸네일 URL 목록 (최대 5개)", example = "[\"https://cdn.cheeeese.com/album/1/thumb1.jpg\"]")
        List<String> recentPhotoUrls
) implements AlbumEnterResponse {}
