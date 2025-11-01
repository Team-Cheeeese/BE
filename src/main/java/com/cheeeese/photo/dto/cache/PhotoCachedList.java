package com.cheeeese.photo.dto.cache;

import lombok.Builder;

import java.util.List;

@Builder
public record PhotoCachedList(
        Long version
) {
}
