package com.cheeeese.album.infrastructure.persistence;

import com.cheeeese.album.domain.Album;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AlbumRepository extends JpaRepository<Album, Long> {
    Optional<Album> findByCode(String code);

    Optional<Album> findByMakerId(Long makerId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Album a SET a.currentParticipant = a.currentParticipant + 1 WHERE a.id = :albumId AND a.currentParticipant < a.participant")
    int incrementParticipantCountAtomically(@Param("albumId") Long albumId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        UPDATE Album a
        SET a.currentParticipant = a.currentParticipant - 1
        WHERE a.id = :albumId
        AND a.currentParticipant > 0
    """)
    void decrementParticipantCount(@Param("albumId") Long albumId);

    @Query("""
        SELECT COUNT(a)
        FROM Album a
        WHERE a.makerId = :userId
        AND a.createdAt >= :start
        AND a.createdAt < :end
    """)
    long countByUserAndCreatedAtBetween(
            @Param("userId") Long userId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Modifying(flushAutomatically = true)
    @Query("UPDATE Album a SET a.currentPhotoCount = a.currentPhotoCount + :count WHERE a.id = :albumId AND a.currentPhotoCount + :count <= a.maxPhotoCount")
    int incrementPhotoCount(@Param("albumId") Long albumId, @Param("count") int count);

    @Modifying(flushAutomatically = true)
    @Query("UPDATE Album a SET a.currentPhotoCount = a.currentPhotoCount - :count WHERE a.id = :albumId AND a.currentPhotoCount >= :count")
    int decrementPhotoCount(@Param("albumId") Long albumId, @Param("count") int count);

    @Modifying
    @Query("UPDATE Album a SET a.status = :status WHERE a.id = :id AND a.status <> :status")
    void updateStatus(Long id, Album.AlbumStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Album a WHERE a.id = :id")
    Album findByIdForUpdate(@Param("id") Long id);

    boolean existsByMakerId(Long makerId);

    @Query("""
        SELECT a.currentPhotoCount
        FROM Album a
        WHERE a.id = :id
    """)
    int findCurrentPhotoCountById(@Param("id") Long id);

    @Query("""
        SELECT a.currentParticipant
        FROM Album a
        WHERE a.id = :albumId
    """)
    int findCurrentParticipant(@Param("albumId") Long albumId);

    @Query("""
        SELECT a.makerId
        FROM Album a
        WHERE a.code = :code
    """)
    Long findAlbumMakerIdByCode(@Param("code") String code);

    @Query("""
        SELECT a
        FROM Album a
        WHERE a.status = :status
            AND a.expiredAt >= :start
            AND a.expiredAt < :end
    """)
    List<Album> findAlbumsExpiringBetween(
            @Param("status") Album.AlbumStatus status,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}
