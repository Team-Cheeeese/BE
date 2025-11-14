package com.cheeeese.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "사용자 기본 정보 응답")
public record UserInfoResponse(
        @Schema(description = "사용자 프로필 이미지 URL", example = "https://cdn.cheeeese.me/profile.png")
        String profileImage,

        @Schema(description = "사용자 이름", example = "치즈러버")
        String name,

        @Schema(description = "사용자가 속한 앨범 수", example = "5")
        long albumCount,

        @Schema(description = "사용자가 업로드한 사진 수", example = "42")
        long photoCount,

        @Schema(description = "사용자가 받은 총 좋아요 수", example = "128")
        long likesCount
) {
}
