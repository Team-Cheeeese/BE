package com.cheeeese.album.application;

import java.util.List;

public record AlbumStorageDeleteEvent(
        Long albumId,
        List<PhotoObjectDeleteTarget> photoObjectTargets
) {
    public record PhotoObjectDeleteTarget(
            String imageUrl,
            String thumbnailUrl,
            boolean deleteOriginal
    ) {}
}
