package com.cheeeese.photo.infrastructure.persistence;

import com.cheeeese.photo.domain.PhotoLikes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PhotoLikesRepository extends JpaRepository<PhotoLikes, Long> {
    @Query("""
        SELECT pl.photo.id
        FROM PhotoLikes pl
        WHERE pl.user.id = :userId
        AND pl.photo.id IN :photoIds
    """)
    List<Long> findAllLikedPhotoIds(@Param("userId") Long userId, @Param("photoIds") List<Long> photoIds);
}
