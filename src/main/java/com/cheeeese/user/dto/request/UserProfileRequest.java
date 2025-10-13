package com.cheeeese.user.dto.request;

import lombok.Builder;

@Builder
public record UserProfileRequest(
        String name
) {
}
