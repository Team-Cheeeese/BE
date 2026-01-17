package com.cheeeese.photo.infrastructure.persistence;

import com.cheeeese.photo.domain.PhotoHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface PhotoHistoryRepository extends JpaRepository<PhotoHistory, Long> {
    boolean existsByUserIdAndPhotoId(Long userId, Long photoId);

    boolean existsByUserIdAndPhotoIdAndUpdatedAtAfter(Long userId, Long photoId, LocalDateTime updatedAt);

    @Query("""
        SELECT ph.photo.id
        FROM PhotoHistory ph
        WHERE ph.user.id = :userId
        AND ph.photo.id IN :photoIds
    """)
    Set<Long> findDownloadedPhotoIds(@Param("userId") Long userId, @Param("photoIds") List<Long> photoIds);

    @Query("""
        SELECT ph.photo.id
        FROM PhotoHistory ph
        WHERE ph.user.id = :userId
        AND ph.photo.id IN :photoIds
        AND ph.updatedAt >= :threshold
    """)
    Set<Long> findRecentlyDownloadedPhotoIds(
            @Param("userId") Long userId,
            @Param("photoIds") List<Long> photoIds,
            @Param("threshold") LocalDateTime threshold
    );

    Optional<PhotoHistory> findByUserIdAndPhotoId(Long userId, Long photoId);

    @Query("""
        SELECT COUNT(DISTINCT ph.user.id)
        FROM PhotoHistory ph
        JOIN ph.photo p
        WHERE p.album.id = :albumId
    """)
    int countDistinctDownloadUsersByAlbumId(@Param("albumId") Long albumId);
}
