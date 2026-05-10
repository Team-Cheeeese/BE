package com.cheeeese.album.domain.event;

import lombok.Builder;

@Builder
public record AlbumJoinedEvent(
        Long userId,
        Long albumId
) {
    public static AlbumJoinedEvent of(Long userId, Long albumId) {
        return AlbumJoinedEvent.builder()
                .userId(userId)
                .albumId(albumId)
                .build();
    }
}
