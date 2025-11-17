package com.cheeeese.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Builder
@Schema(description = "사용자가 선택할 수 있는 프로필 이미지 목록 API")
public record UserProfileImageResponse(
        @Schema(
                description = "프로필 이미지 옵션 목록",
                example = """
                        [
                          {
                            "imageCode": "P5",
                            "profileImageUrl": "https://say-cheese-profile.edge.naverncp.com/profile/sign_up_profile_5.jpg"
                          },
                          {
                            "imageCode": "P6",
                            "profileImageUrl": "https://say-cheese-profile.edge.naverncp.com/profile/sign_up_profile_6.jpg"
                          }
                        ]
                        """
        )
        List<ProfileImageOpt> opts
) {

    @Builder
    public record ProfileImageOpt(
            @Schema(
                    description = "프로필 이미지 코드",
                    example = "P5"
            )
            String imageCode,

            @Schema(
                    description = "프로필 이미지의 CDN URL",
                    example = "https://say-cheese-profile.edge.naverncp.com/profile/signup_profile_5.jpg"
            )
            String profileImageUrl
    ) {}
}
