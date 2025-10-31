package com.cheeeese.album.dto.response;

import com.cheeeese.album.domain.type.AlbumJoinStatus;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ExistingEnterResponse(
        AlbumJoinStatus joinStatus,
        String title,
        String themeEmoji,
        String eventDate,
        LocalDateTime expiredAt,
        AlbumMakerInfo makerInfo
) implements AlbumEnterResponse {}
