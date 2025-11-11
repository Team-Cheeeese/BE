package com.cheeeese.cheese4cut.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "Presigned URL 발급 응답")
public record Cheese4cutPresignedUrlResponse (
    @Schema(description = "클라우드 스토리지에 업로드할 URL", example = "https://bucket.s3.ap-northeast-2.amazonaws.com/...")
    String uploadUrl
){}