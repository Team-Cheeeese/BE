package com.cheeeese.user.domain.model;

import lombok.Builder;

@Builder
public record UserStatsImpact(
        int photoCnt,
        int likesCnt
) {
    public static UserStatsImpact of(int photoCnt, int likesCnt) {
        return UserStatsImpact.builder()
                .photoCnt(photoCnt)
                .likesCnt(likesCnt)
                .build();
    }
}
