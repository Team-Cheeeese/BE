package com.cheeeese.photo.infrastructure.persistence;

import com.cheeeese.photo.domain.PhotoHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface PhotoHistoryRepository extends JpaRepository<PhotoHistory, Long> {
    @Query("""
        SELECT ph.photo.id
        FROM PhotoHistory ph
        WHERE ph.user.id = :userId
        AND ph.photo.id IN :photoIds
    """)
    List<Long> findDownloadedPhotoIdsByUserId(@Param("userId") Long userId, @Param("photoIds") List<Long> photoIds);

    @Query("""
        SELECT ph.photo.id
        FROM PhotoHistory ph
        WHERE ph.user.id = :userId
        AND ph.photo.id IN :photoIds
        AND ph.createdAt >= :threshold
    """)
    List<Long> findRecentlyDownloadedPhotoIdsByUserId(
            @Param("userId") Long userId,
            @Param("photoIds") List<Long> photoIds,
            @Param("threshold") LocalDateTime threshold
    );

    boolean existsByUserIdAndPhotoId(Long userId, Long photoId);

    boolean existsByUserIdAndPhotoIdAndCreatedAt(Long userId, Long photoId, LocalDateTime createdAt);
}
