package com.cheeeese.fixture;

import com.cheeeese.album.domain.Album;
import com.cheeeese.album.domain.UserAlbum;
import com.cheeeese.album.domain.type.Role;
import com.cheeeese.album.infrastructure.mapper.AlbumMapper;
import com.cheeeese.album.infrastructure.mapper.UserAlbumMapper;
import com.github.f4b6a3.uuid.UuidCreator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class FixtureFactory {

    public static Album createAlbum(Long userId) {
        return AlbumMapper.toEntity(
                userId,
                "테스트 앨범",
                "테스트 코드",
                "테스트 이미지",
                4,
                LocalDate.of(2025, 1, 1),
                true,
                LocalDateTime.now().plusDays(7),
                true
        );
    }

    public static Album createAlbumV4(int i) {
        return AlbumMapper.toEntity(
                1L,
                "v4-" + i,
                UUID.randomUUID().toString(),
                "테스트 이미지",
                4,
                LocalDate.of(2025, 1, 1),
                true,
                LocalDateTime.now().plusDays(7),
                true
        );
    }

    public static Album createAlbumV7(int i) {
        return AlbumMapper.toEntity(
                1L,
                "v7-" + i,
                UuidCreator.getTimeOrderedEpoch().toString(),
                "테스트 이미지",
                4,
                LocalDate.of(2025, 1, 1),
                true,
                LocalDateTime.now().plusDays(7),
                true
        );
    }

    public static UserAlbum createHostUserAlbum(Long userId, Long albumId) {
        return UserAlbumMapper.toEntity(
                userId,
                albumId,
                Role.MAKER
        );
    }
}
