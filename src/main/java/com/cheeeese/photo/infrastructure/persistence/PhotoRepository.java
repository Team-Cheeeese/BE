package com.cheeeese.photo.infrastructure.persistence;

import com.cheeeese.photo.domain.Photo;
import com.cheeeese.photo.domain.PhotoStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PhotoRepository extends JpaRepository<Photo, Long> {

    @Query("""
        SELECT p 
        FROM Photo p 
        JOIN FETCH p.user
        WHERE p.album.id = :albumId 
        AND p.isDeleted = FALSE 
        AND p.status = :status
        ORDER BY p.createdAt DESC
    """)
    List<Photo> findRecentPhotosByAlbumIdAndStatus(
            @Param("albumId") Long albumId,
            @Param("status") PhotoStatus status,
            Pageable pageable
    );

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        UPDATE Photo p
        SET p.status = :newStatus
        WHERE p.id IN :photoIds
        AND p.user.id = :userId
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

    @Query("""
    SELECT p.id
    FROM Photo p
    WHERE p.album.id = :albumId
    AND p.isDeleted = FALSE
    AND p.status = com.cheeeese.photo.domain.PhotoStatus.COMPLETED
    ORDER BY p.likesCnt DESC, p.createdAt DESC
""")
    List<Long> findTop4CompletedPhotoIdsByLikes(
            @Param("albumId") Long albumId,
            Pageable pageable
    );
}
