package com.cheeeese.photo.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Builder
@Schema(description = "사진 목록 조회 페이지네이션")
public record PhotoPageResponse(
        @Schema(
                description = "사진 목록",
                example = """
                        [
                          {
                            "photoId": 1,
                            "thumbnailUrl": "https://cdn.cheeeese.me/thumb1.jpg",
                            "likesCnt": 1,
                            "isLiked": true,
                            "isDownloaded": false,
                            "isRecentlyDownloaded": false
                          }
                        ]
                        """
        )
        List<PhotoListResponse> responses,

        @Schema(description = "현재 페이지의 사진 개수", example = "20")
        int listSize,

        @Schema(description = "첫 번째 페이지 여부", example = "true")
        boolean isFirst,

        @Schema(description = "마지막 페이지 여부", example = "false")
        boolean isLast,

        @Schema(description = "다음 페이지 존재 여부", example = "true")
        boolean hasNext
) {
}
