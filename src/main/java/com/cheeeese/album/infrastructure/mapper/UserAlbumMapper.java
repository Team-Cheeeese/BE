package com.cheeeese.album.infrastructure.mapper;

import com.cheeeese.album.domain.Album;
import com.cheeeese.album.domain.UserAlbum;
import com.cheeeese.album.domain.type.Role;
import com.cheeeese.album.dto.response.AlbumParticipantListResponse;
import com.cheeeese.album.dto.response.AlbumParticipantResponse;
import com.cheeeese.user.domain.User;

import java.util.List;

public class UserAlbumMapper {

    public static UserAlbum toEntity(User user, Album album, Role role) {
        return UserAlbum.builder()
                .user(user)
                .album(album)
                .role(role)
                .isVisible(true)
                .build();
    }

    public static AlbumParticipantListResponse.ParticipantInfo toParticipantInfo(User user, String profileImage, Role role, boolean isMe) {
        return AlbumParticipantListResponse.ParticipantInfo.builder()
                .name(user.getName())
                .role(role)
                .profileImage(profileImage)
                .isMe(isMe)
                .build();
    }

    public static AlbumParticipantResponse toAlbumParticipantResponse(
            Album album,
            boolean isExpired,
            Role myRole,
            List<AlbumParticipantListResponse.ParticipantInfo> participants
    ) {
        return AlbumParticipantResponse.builder()
                .isExpired(isExpired)
                .title(album.getTitle())
                .themeEmoji(album.getThemeEmoji())
                .eventDate(album.getEventDate())
                .expiredAt(album.getExpiredAt())
                .maxParticipantCount(album.getParticipant())
                .currentParticipantCount(album.getCurrentParticipant())
                .participants(participants)
                .myRole(myRole)
                .build();
    }
}
