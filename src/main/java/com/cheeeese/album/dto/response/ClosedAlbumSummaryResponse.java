package com.cheeeese.album.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Builder
@Schema(
        description = "닫힌 앨범 요약 정보",
        requiredProperties = {
                "code",
                "title",
                "makerName",
                "eventDate"
        }
)
public record ClosedAlbumSummaryResponse(
        @Schema(description = "앨범 코드", example = "786ccd09-...")
        String code,

        @Schema(description = "앨범 제목", example = "봄 소풍")
        String title,

        @Schema(description = "앨범 생성자 이름", example = "치즈메이커")
        String makerName,

        @Schema(description = "이벤트 날짜", example = "2025-05-01")
        LocalDate eventDate,

        @Schema(description = "치즈네컷 썸네일 목록 (4개)", nullable = true)
        List<String> thumbnails
) {}
