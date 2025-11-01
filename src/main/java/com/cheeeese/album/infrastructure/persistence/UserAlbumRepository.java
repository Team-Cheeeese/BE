package com.cheeeese.album.infrastructure.persistence;

import com.cheeeese.album.domain.UserAlbum;
import com.cheeeese.album.domain.type.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserAlbumRepository extends JpaRepository<UserAlbum, Long> {
    @Query("SELECT ua FROM UserAlbum ua WHERE ua.user.id = :userId AND ua.album.id = :albumId")
    Optional<UserAlbum> findByUserIdAndAlbumId(@Param("userId") Long userId, @Param("albumId") Long albumId);

    @Query("SELECT ua FROM UserAlbum ua JOIN FETCH ua.user WHERE ua.album.id = :albumId")
    List<UserAlbum> findAllByAlbumId(@Param("albumId") Long albumId);

    @Query("SELECT ua FROM UserAlbum ua WHERE ua.album.id = :albumId AND ua.user.id = :userId AND ua.role = :role")
    Optional<UserAlbum> findByAlbumIdAndUserIdAndRole(@Param("albumId") Long albumId, @Param("userId") Long userId, @Param("role") Role role);
}
