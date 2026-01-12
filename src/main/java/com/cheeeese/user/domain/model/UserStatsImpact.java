package com.cheeeese.user.domain.model;

import lombok.Builder;

@Builder
public record UserStatsImpact(
        int photoCnt,
        int likeCnt
) {
    public static UserStatsImpact of(int photoCnt, int likeCnt) {
        return UserStatsImpact.builder()
                .photoCnt(photoCnt)
                .likeCnt(likeCnt)
                .build();
    }
}
