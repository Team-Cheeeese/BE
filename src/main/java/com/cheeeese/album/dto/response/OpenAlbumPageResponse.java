package com.cheeeese.album.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Builder
@Schema(
        description = "열린 앨범 목록 페이지 응답",
        requiredProperties = {
                "responses",
                "listSize",
                "isFirst",
                "isLast",
                "hasNext"
        }
)
public record OpenAlbumPageResponse(
        @Schema(description = "열린 앨범 목록")
        List<OpenAlbumSummaryResponse> responses,

        @Schema(description = "현재 페이지의 앨범 수", example = "2")
        int listSize,

        @Schema(description = "첫 페이지 여부", example = "true")
        boolean isFirst,

        @Schema(description = "마지막 페이지 여부", example = "false")
        boolean isLast,

        @Schema(description = "다음 페이지 존재 여부", example = "true")
        boolean hasNext
) {
}
