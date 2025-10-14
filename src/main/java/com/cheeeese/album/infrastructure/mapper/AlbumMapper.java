package com.cheeeese.album.infrastructure.mapper;

import com.cheeeese.album.domain.Album;
import com.cheeeese.album.dto.response.AlbumInvitationResponse;
import com.cheeeese.user.domain.User;

public class AlbumMapper {

    /**
     * Album 엔티티와 Host User 정보를 초대장 응답 DTO로 변환합니다.
     */
    public static AlbumInvitationResponse toInvitationResponse(Album album, User host) {
        return AlbumInvitationResponse.builder()
                .title(album.getTitle())
                .themeImageUrl(album.getThemeImageUrl())
                .eventDate(album.getEventDate().toString())
                .expiredAt(album.getExpiredAt())
                .hostName(host.getName())
                .hostProfileImage(host.getProfileImage())
                .build();
    }
}