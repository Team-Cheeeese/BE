package com.cheeeese.album.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(
        description = "앨범 메이커 정보 응답 DTO",
        requiredProperties = {
                "makerName",
                "makerProfileImage"
        }
)
@Builder
public record AlbumMakerInfo(
        @Schema(description = "메이커 이름", example = "우다현")
        String makerName,

        @Schema(description = "메이커 프로필 이미지 URL", example = "https://cdn.cheeeese.com/users/1/profile.jpg")
        String makerProfileImage
) {}
