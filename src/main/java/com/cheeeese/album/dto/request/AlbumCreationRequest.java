package com.cheeeese.album.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDate;

@Builder
@Schema(description = "앨범 생성 API")
public record AlbumCreationRequest(
        @Schema(description = "썸네일 이미지", example = "https://theme.jpg")
        String themeImageUrl,

        @Schema(description = "앨범 이름", example = "졸업식")
        String title,

        @Schema(description = "참여자 수", example = "64")
        int participant,

        @Schema(description = "행사 날짜", example = "2025-02-01")
        LocalDate eventDate,

        @Schema(description = "앨범 생성 필수 약관 동의", example = "true")
        boolean isTermsAgreement
) {
}
