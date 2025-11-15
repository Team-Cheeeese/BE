package com.cheeeese.user.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record UserProfileImageResponse(
        List<ProfileImageOpt> opts
) {

    @Builder
    public record ProfileImageOpt(
            String imageCode,
            String profileImageUrl
    ) {}
}
