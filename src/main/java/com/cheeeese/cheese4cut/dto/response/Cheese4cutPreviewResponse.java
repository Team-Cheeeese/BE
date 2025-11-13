package com.cheeeese.cheese4cut.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Builder
@Schema(description = "치즈네컷 확정 전 미리보기 응답 DTO (좋아요 TOP 4)")
public record Cheese4cutPreviewResponse(
        @Schema(description = "확정 여부 (항상 false)", example = "false")
        boolean isFinalized,

        @Schema(description = "미리보기 사진 목록 (최대 4개)")
        List<PreviewPhotoInfo> previewPhotos,

        @Schema(description = "4개 사진에 좋아요를 누른 유니크한 참여자 수", example = "5")
        int uniqueLikesCount,

        @Schema(description = "전체 참여자 수", example = "6")
        int participant

) implements Cheese4cutResponse {
    @Builder
    @Schema(description = "미리보기 사진 정보 DTO")
    public record PreviewPhotoInfo(
            @Schema(description = "사진 ID", example = "101")
            Long photoId,

            @Schema(description = "사진 원본 URL", example = "https://cdn.cheeeese.com/album/1/original/101.jpg")
            String imageUrl,

            @Schema(description = "선정 순위 (1~4)", example = "1")
            int photoRank
    ) {}
}
