package com.cheeeese.album.domain.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AlbumSorting {
    POPULAR("popular", "띱 많은순"),
    CAPTURED_AT("captured", "최근 촬영한 시간순"),
    CREATED_AT("uploaded", "최근 업로드된 사진순");

    private final String param;
    private final String description;
}
