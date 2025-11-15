package com.cheeeese.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "사용자 프로필 이미지 수정 API")
public record UserProfileImageRequest(
        @Schema(description = "프로필 이미지 코드", example = "P1")
        String imageCode
) {
}
