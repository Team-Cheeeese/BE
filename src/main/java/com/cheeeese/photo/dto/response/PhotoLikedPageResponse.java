package com.cheeeese.photo.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Builder
@Schema(
        description = "내가 좋아요한 사진 조회 페이지네이션",
        requiredProperties = {
                "responses",
                "listSize",
                "isFirst",
                "isLast",
                "hasNext"
        }
)
public record PhotoLikedPageResponse(
        @Schema(
                description = "사진 목록",
                example = """
                        [
                          {
                            "photoId": 1,
                            "thumbnailUrl": "https://cdn.cheeeese.me/thumb1.jpg",
                            "isDownloaded": false,
                            "isRecentlyDownloaded": false
                          }
                        ]
                        """
        )
        List<PhotoLikedResponse> responses,

        @Schema(description = "현재 페이지의 사진 개수", example = "10")
        int listSize,

        @Schema(description = "첫 번째 페이지 여부", example = "true")
        boolean isFirst,

        @Schema(description = "마지막 페이지 여부", example = "false")
        boolean isLast,

        @Schema(description = "다음 페이지 존재 여부", example = "true")
        boolean hasNext
) {
}
