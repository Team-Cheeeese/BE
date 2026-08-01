package com.cheeeese.photo.application;

public record PhotoCacheInvalidationEvent(
        String albumCode
) {
}
