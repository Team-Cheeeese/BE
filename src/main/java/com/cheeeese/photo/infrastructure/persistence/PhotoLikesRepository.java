package com.cheeeese.photo.infrastructure.persistence;

import com.cheeeese.photo.domain.PhotoLikes;
import com.cheeeese.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface PhotoLikesRepository extends JpaRepository<PhotoLikes, Long> {
    @Query("""
        SELECT pl.photo.id
        FROM PhotoLikes pl
        WHERE pl.user.id = :userId
        AND pl.photo.id IN :photoIds
    """)
    Set<Long> findAllLikedPhotoIds(@Param("userId") Long userId, @Param("photoIds") List<Long> photoIds);

    boolean existsByUserIdAndPhotoId(Long userId, Long photoId);

    Optional<PhotoLikes> findByUserIdAndPhotoId(Long userId, Long photoId);

    @Query("""
        SELECT COUNT(DISTINCT pl.user.id)
        FROM PhotoLikes pl
        WHERE pl.photo.id IN :photoIds
    """)
    long countDistinctUserIdsByPhotoIds(@Param("photoIds") List<Long> photoIds);

    @Query("""
        SELECT pl.user
        FROM PhotoLikes pl
        WHERE pl.photo.id = :photoId
    """)
    List<User> findLikersByPhotoId(@Param("photoId") Long photoId);

    void deleteAllByPhotoId(Long photoId);

    @Query("""
        SELECT count(pl)
        FROM PhotoLikes pl
        JOIN pl.photo p
        WHERE p.album.id = :albumId
        AND p.user.id = :userId
    """)
    int countLikesByAlbumAndPhotoOwner(
            @Param("albumId") Long albumId,
            @Param("userId") Long userId
    );

    @Modifying
    @Query("""
        DELETE FROM PhotoLikes pl
        WHERE pl.photo.id IN :photoIds
    """)
    void deleteAllByPhotoIds(@Param("photoIds") List<Long> photoIds);
}
