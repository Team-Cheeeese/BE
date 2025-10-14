package com.cheeeese.album.infrastructure.persistence;

import com.cheeeese.album.domain.UserAlbum;
import com.cheeeese.album.domain.UserAlbumRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserAlbumRepository extends JpaRepository<UserAlbum, Long> {
    Optional<UserAlbum> findByUserIdAndAlbumId(Long userId, Long albumId);

    List<UserAlbum> findAllByAlbumId(Long albumId);

    Optional<UserAlbum> findByAlbumIdAndUserIdAndRole(Long albumId, Long userId, UserAlbumRole role);
}
