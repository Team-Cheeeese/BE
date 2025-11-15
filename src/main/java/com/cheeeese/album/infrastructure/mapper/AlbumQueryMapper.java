package com.cheeeese.album.infrastructure.mapper;

import com.cheeeese.album.domain.Album;
import com.cheeeese.album.dto.response.ClosedAlbumPageResponse;
import com.cheeeese.album.dto.response.ClosedAlbumSummaryResponse;
import com.cheeeese.album.dto.response.OpenAlbumPageResponse;
import com.cheeeese.album.dto.response.OpenAlbumSummaryResponse;
import com.cheeeese.user.domain.User;
import org.springframework.data.domain.Slice;

import java.util.List;

public class AlbumQueryMapper {

    public static OpenAlbumPageResponse toOpenAlbumPageResponse(
            List<OpenAlbumSummaryResponse> responses,
            Slice<Album> albums
    ){
        return OpenAlbumPageResponse.builder()
                .responses(responses)
                .listSize(responses.size())
                .isFirst(albums.isFirst())
                .isLast(albums.isLast())
                .hasNext(albums.hasNext())
                .build();
    }

    public static ClosedAlbumPageResponse toClosedAlbumPageResponse(
            List<ClosedAlbumSummaryResponse> responses,
            Slice<Album> albums
    ){
        return ClosedAlbumPageResponse.builder()
                .responses(responses)
                .listSize(responses.size())
                .isFirst(albums.isFirst())
                .isLast(albums.isLast())
                .hasNext(albums.hasNext())
                .build();
    }

    public static OpenAlbumSummaryResponse toOpenAlbumSummaryResponse(
            Album album,
            User maker,
            List<String> thumbnails
    ){
        return OpenAlbumSummaryResponse.builder()
                .code(album.getCode())
                .themeEmoji(album.getThemeEmoji())
                .title(album.getTitle())
                .eventDate(album.getEventDate())
                .makerName(maker != null ? maker.getName() : null)
                .currentParticipant(album.getCurrentParticipant())
                .participant(album.getParticipant())
                .expiredAt(album.getExpiredAt())
                .recentPhotoThumbnails(thumbnails.isEmpty() ? null : thumbnails)
                .build();
    }

    public static ClosedAlbumSummaryResponse toClosedAlbumSummaryResponse(
            Album album,
            User maker,
            List<String> thumbnails
    ) {
        return ClosedAlbumSummaryResponse.builder()
                .code(album.getCode())
                .title(album.getTitle())
                .makerName(maker != null ? maker.getName() : null)
                .eventDate(album.getEventDate())
                .thumbnails(thumbnails.isEmpty() ? null : thumbnails)
                .build();
    }

}
