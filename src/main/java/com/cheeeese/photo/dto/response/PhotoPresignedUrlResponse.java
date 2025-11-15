package com.cheeeese.photo.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Builder
@Schema(
        description = "Presigned URL 발급 응답",
        requiredProperties = {
                "presignedUrlInfos"
        }
)
public record PhotoPresignedUrlResponse(
        @Schema(description = "발급된 Presigned URL 목록")
        List<PresignedUrlInfo> presignedUrlInfos
) {
    @Builder
    @Schema(
            description = "Presigned URL 정보",
            requiredProperties = {
                    "photoId",
                    "uploadUrl"
            }
    )
    public record PresignedUrlInfo(
            @Schema(description = "저장된 사진 ID", example = "100")
            Long photoId,

            @Schema(description = "클라우드 스토리지에 업로드할 URL", example = "https://bucket.s3.ap-northeast-2.amazonaws.com/...")
            String uploadUrl
    ) {}
}