package com.cheeeese.album.infrastructure.persistence;

import com.cheeeese.album.domain.Album;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
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

    @Modifying
    @Query("""
        UPDATE Album a
        SET a.currentParticipant = a.currentParticipant + 1
        WHERE a.id = :albumId
    """)
    int incrementParticipantCount(@Param("albumId") Long albumId);

    @Modifying
    @Query("""
        UPDATE Album a
        SET a.participantMilestoneAt = :now
        WHERE a.id = :albumId
        AND a.currentParticipant = 2
        AND a.participantMilestoneAt IS NULL
    """)
    int markParticipants2Milestone(
            @Param("albumId") Long albumId,
            @Param("now") LocalDateTime now
    );

    @Modifying(clearAutomatically = true)
    @Query("""
        UPDATE Album a
        SET a.downloadUserCount = a.downloadUserCount + 1
        WHERE a.id = :albumId
    """)
    void incrementDownloadUserCount(@Param("albumId") Long albumId);

    @Query("""
        SELECT a.downloadUserCount
        FROM Album a
        WHERE a.id = :albumId
    """)
    int findDownloadUserCount(@Param("albumId") Long albumId);
}
