package com.cheeeese.album.dto.response;

import com.cheeeese.album.domain.type.AlbumJoinStatus;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "앨범 입장 응답 (다형 구조)")
public sealed interface AlbumEnterResponse
        permits NewEnterResponse, ExistingEnterResponse {
    AlbumJoinStatus joinStatus();
}