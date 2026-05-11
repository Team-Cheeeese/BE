package com.cheeeese.cheese4cut.domain.event;

import lombok.Builder;

@Builder
public record Cheese4cutCreatedEvent(
        Long albumId,
        Long cheese4cutId
) {
    public static Cheese4cutCreatedEvent of(Long albumId, Long cheese4cutId) {
        return Cheese4cutCreatedEvent.builder()
                .albumId(albumId)
                .cheese4cutId(cheese4cutId)
                .build();
    }
}
