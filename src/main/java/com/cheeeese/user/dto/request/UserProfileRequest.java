package com.cheeeese.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "사용자 프로필 수정 API")
public record UserProfileRequest(
        @Schema(description = "사용자 이름", example = "주")
        String name
        // TODO: 이미지 수정 추후 추가
) {
}
