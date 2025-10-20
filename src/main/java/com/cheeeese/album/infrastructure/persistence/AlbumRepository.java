package com.cheeeese.album.infrastructure.persistence;

import com.cheeeese.album.domain.Album;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface AlbumRepository extends JpaRepository<Album, Long> {
    Optional<Album> findByCode(String code);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Album a SET a.currentParticipant = a.currentParticipant + 1 WHERE a.id = :albumId AND a.currentParticipant < a.participant")
    int incrementParticipantCountAtomically(@Param("albumId") Long albumId);

    @Query("""
        SELECT COUNT(a)
        FROM Album a
        WHERE a.hostId = :userId
        AND a.createdAt >= :start
        AND a.createdAt < :end
    """)
    long countByUserAndCreatedAtBetween(
            @Param("userId") Long userId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}
