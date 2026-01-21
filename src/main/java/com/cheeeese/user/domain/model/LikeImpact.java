package com.cheeeese.user.domain.model;

import lombok.Builder;

@Builder
public record LikeImpact(
        Long ownerId,
        Long likeCnt
) {
}
