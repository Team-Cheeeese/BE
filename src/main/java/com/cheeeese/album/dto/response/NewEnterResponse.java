package com.cheeeese.album.dto.response;

import com.cheeeese.album.domain.type.AlbumJoinStatus;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record NewEnterResponse(
        AlbumJoinStatus joinStatus,
        String title,
        String themeEmoji,
        String eventDate,
        LocalDateTime expiredAt,
        AlbumMakerInfo makerInfo,
        Integer remainingUploadSlots,
        List<String> recentPhotoUrls
) implements AlbumEnterResponse {}
