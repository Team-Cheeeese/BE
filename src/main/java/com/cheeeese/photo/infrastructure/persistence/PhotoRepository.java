package com.cheeeese.photo.infrastructure.persistence;

import com.cheeeese.photo.domain.Photo;
import com.cheeeese.photo.domain.PhotoStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PhotoRepository extends JpaRepository<Photo, Long> {

    List<Photo> findTop5ByAlbumIdAndIsDeletedFalseAndStatusOrderByCreatedAtDesc(
            Long albumId,
            PhotoStatus status
    );
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        UPDATE Photo p
        SET p.status = :newStatus
        WHERE p.id IN :photoIds
        AND p.userId = :userId
        AND p.status = :expectedStatus
    """)
    int updateStatusByIdsAndUserIdAndExpectedStatus(
            @Param("photoIds") List<Long> photoIds,
            @Param("userId") Long userId,
            @Param("newStatus") PhotoStatus newStatus,
            @Param("expectedStatus") PhotoStatus expectedStatus
    );

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Photo p set p.status = :newStatus, p.thumbnailUrl = :thumbnailUrl " +
            "where p.id = :photoId and p.status = :expectedStatus")
    int updateStatusAndUrl(Long photoId, PhotoStatus expectedStatus, PhotoStatus newStatus, String thumbnailUrl);
}
