package com.cheeeese.photo.infrastructure.persistence;

import com.cheeeese.photo.domain.Photo;
import com.cheeeese.photo.domain.PhotoStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PhotoRepository extends JpaRepository<Photo, Long> {

    Optional<Photo> findByIdAndAlbum_Code(Long photoId, String albumCode);

    @Query("""
        SELECT p
        FROM Photo p
        JOIN p.album a
        WHERE a.code = :code
        AND p.isDeleted = FALSE
        AND p.status = :status
    """)
    Slice<Photo> findAllByAlbumCodeAndStatus(
            @Param("code") String code,
            @Param("status") PhotoStatus status,
            Pageable pageable
    );

    @Query("""
        SELECT p
        FROM Photo p
        JOIN p.album a
        JOIN PhotoLikes pl ON pl.photo = p
        WHERE a.code = :albumCode
        AND pl.user.id = :userId
    """)
    Slice<Photo> findLikedPhotosByAlbumAndUser(
            @Param("albumCode") String albumCode,
            @Param("userId") Long userId,
            Pageable pageable
    );

    @Modifying
    @Query("""
        UPDATE Photo p
        SET p.likesCnt = p.likesCnt + 1
        WHERE p.id = :photoId
    """)
    void incrementLikeCnt(@Param("photoId") Long photoId);

    @Modifying
    @Query("""
        UPDATE Photo p
        SET p.likesCnt = p.likesCnt - 1
        WHERE p.id = :photoId
        AND p.likesCnt > 0
    """)
    void decrementLikeCnt(@Param("photoId") Long photoId);

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
        AND p.status = :status
        ORDER BY p.likesCnt DESC, p.createdAt DESC
    """)
    List<Long> findTop4CompletedPhotoIdsByLikes(
            @Param("albumId") Long albumId,
            @Param("status") PhotoStatus status,
            Pageable pageable
    );

    @Query("""
        SELECT p
        FROM Photo p
        WHERE p.album.id = :albumId
        AND p.isDeleted = FALSE
        AND p.status = :status
        ORDER BY p.likesCnt DESC, p.createdAt DESC
    """)
    List<Photo> findTop4CompletedPhotosByLikes(
            @Param("albumId") Long albumId,
            @Param("status") PhotoStatus status,
            Pageable pageable
    );

    List<Photo> findAllByIdIn(List<Long> photoIds);

    @Query("""
    SELECT p
    FROM Photo p
    WHERE p.id IN :photoIds
      AND p.isDeleted = FALSE
    ORDER BY p.likesCnt DESC, p.createdAt DESC, p.id DESC
    """)
    List<Photo> findAllByIdInOrderByLikesDescCreatedDesc(@Param("photoIds") List<Long> photoIds);

    @Query("""
        SELECT p.album.code
        FROM Photo p
        WHERE p.id = :photoId
    """)
    String findAlbumCodeByPhotoId(@Param("photoId") Long photoId);
}
