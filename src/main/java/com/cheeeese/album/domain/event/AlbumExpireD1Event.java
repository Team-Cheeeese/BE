package com.cheeeese.album.domain.event;

import lombok.Builder;

@Builder
public record AlbumExpireD1Event(
        Long albumId
) {
    public static AlbumExpireD1Event of(Long albumId) {
        return AlbumExpireD1Event.builder()
                .albumId(albumId)
                .build();
    }
}
