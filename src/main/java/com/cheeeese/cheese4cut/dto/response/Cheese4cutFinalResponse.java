package com.cheeeese.cheese4cut.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Builder
@Schema(
        description = "치즈네컷 확정 완료 응답 DTO (선정된 사진 4장)",
        requiredProperties = {
                "isFinalized",
                "photos"
        }
)
public record Cheese4cutFinalResponse(
        @Schema(description = "확정 여부 (항상 true)", example = "true")
        boolean isFinalized,

        @Schema(description = "확정된 사진 정보 목록 (정확히 4개)")
        List<FinalPhotoInfo> photos
) implements Cheese4cutResponse {
        @Builder
        @Schema(
                description = "확정된 사진 정보",
                requiredProperties = {
                        "photoId",
                        "imageUrl",
                        "photoRank"
                }
        )
        public record FinalPhotoInfo(
                @Schema(description = "사진 ID", example = "101")
                Long photoId,

                @Schema(description = "사진 썸네일 URL", example = "https://cdn.cheeeese.com/album/1/original/101.jpg")
                String imageUrl,

                @Schema(description = "선정 순위 (1~4)", example = "1")
                int photoRank
        ) {}
}
