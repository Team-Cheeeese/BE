package com.cheeeese.album.infrastructure.mapper;

import com.cheeeese.album.domain.Album;
import com.cheeeese.album.domain.UserAlbum;
import com.cheeeese.album.domain.type.Role;
import com.cheeeese.user.domain.User;

public class UserAlbumMapper {

    public static UserAlbum toEntity(User user, Album album, Role role) {
        return UserAlbum.builder()
                .user(user)
                .album(album)
                .role(role)
                .isVisible(true)
                .build();
    }
}
