package com.cheeeese.album.infrastructure.mapper;

import com.cheeeese.album.domain.UserAlbum;
import com.cheeeese.album.domain.type.Role;

public class UserAlbumMapper {

    public static UserAlbum toEntity(Long userId, Long albumId, Role role) {
        return UserAlbum.builder()
                .userId(userId)
                .albumId(albumId)
                .role(role)
                .build();
    }
}
