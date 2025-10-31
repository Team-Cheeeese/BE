package com.cheeeese.album.infrastructure.mapper;

import com.cheeeese.album.domain.Album;
import com.cheeeese.album.domain.UserAlbum;
import com.cheeeese.album.domain.type.Role;
import com.cheeeese.user.domain.User;

public class UserAlbumMapper {

    public static UserAlbum toEntity(Long userId, Long albumId, Role role) {
        return UserAlbum.builder()
                .userId(userId)
                .albumId(albumId)
                .role(role)
                .isVisible(true)
                .build();
    }

    /**
     * User와 Album 엔티티를 기반으로 GUEST 역할의 UserAlbum 엔티티를 생성합니다.
     */
    public static UserAlbum toGuestUserAlbum(User user, Album album) {
        return UserAlbum.builder()
                .userId(user.getId())
                .albumId(album.getId())
                .role(Role.GUEST)
                .isVisible(true)
                .build();
    }
}
