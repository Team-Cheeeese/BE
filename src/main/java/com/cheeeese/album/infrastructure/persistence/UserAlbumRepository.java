package com.cheeeese.album.infrastructure.persistence;

import com.cheeeese.album.domain.Album;
import com.cheeeese.album.domain.UserAlbum;
import com.cheeeese.album.domain.type.Role;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserAlbumRepository extends JpaRepository<UserAlbum, Long> {
    @Query("SELECT ua FROM UserAlbum ua WHERE ua.user.id = :userId AND ua.album.id = :albumId")
    Optional<UserAlbum> findByUserIdAndAlbumId(@Param("userId") Long userId, @Param("albumId") Long albumId);

    @Query("""
        SELECT ua
        FROM UserAlbum ua
        JOIN FETCH ua.user
        WHERE ua.album.id = :albumId
        AND ua.role <> :role
    """)
    List<UserAlbum> findAllByAlbumIdExcludeBlack(
            @Param("albumId") Long albumId,
            @Param("role") Role role
    );


    @Query("SELECT ua FROM UserAlbum ua WHERE ua.album.id = :albumId AND ua.user.id = :userId AND ua.role = :role")
    Optional<UserAlbum> findByAlbumIdAndUserIdAndRole(@Param("albumId") Long albumId, @Param("userId") Long userId, @Param("role") Role role);

    @Query("""
        SELECT a
        FROM UserAlbum ua
        JOIN ua.album a
        WHERE ua.user.id = :userId
          AND ua.isVisible = TRUE
          AND a.status = :status
          AND a.expiredAt > :now
        ORDER BY a.expiredAt ASC
    """)
    Slice<Album> findOpenAlbumsByUserId(
            @Param("userId") Long userId,
            @Param("status") Album.AlbumStatus status,
            @Param("now") LocalDateTime now,
            Pageable pageable
    );

    @Query("""
        SELECT a
        FROM UserAlbum ua
        JOIN ua.album a
        WHERE ua.user.id = :userId
          AND ua.role = :role
          AND ua.isVisible = TRUE
          AND a.status = :status
          AND a.expiredAt > :now
        ORDER BY a.expiredAt ASC
    """)
    Slice<Album> findOpenAlbumsByUserIdAndRole(
            @Param("userId") Long userId,
            @Param("role") Role role,
            @Param("status") Album.AlbumStatus status,
            @Param("now") LocalDateTime now,
            Pageable pageable
    );

    @Query("""
        SELECT a
        FROM UserAlbum ua
        JOIN ua.album a
        WHERE ua.user.id = :userId
          AND ua.isVisible = TRUE
          AND a.status = :status
        ORDER BY a.createdAt DESC
    """)
    Slice<Album> findClosedAlbumsByUserId(
            @Param("userId") Long userId,
            @Param("status") Album.AlbumStatus status,
            Pageable pageable
    );

    @Query("""
        SELECT ua
        FROM UserAlbum ua
        JOIN FETCH ua.user
        WHERE ua.album.id = :albumId
        AND ua.role = :role
    """)
    Optional<UserAlbum> findMakerByAlbumId(@Param("albumId") Long albumId, @Param("role") Role role);

    @Query("""
        SELECT ua
        FROM UserAlbum ua
        JOIN FETCH ua.user
        WHERE ua.album.id = :albumId
        AND ua.isVisible = TRUE
        AND ua.role <> :blackRole
    """)
    List<UserAlbum> findNotificationParticipants(
            @Param("albumId") Long albumId,
            @Param("blackRole") Role blackRole
    );
}
