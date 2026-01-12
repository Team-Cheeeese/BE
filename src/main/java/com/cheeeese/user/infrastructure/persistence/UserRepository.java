package com.cheeeese.user.infrastructure.persistence;

import com.cheeeese.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByProviderId(String providerId);

    @Modifying(flushAutomatically = true)
    @Query("UPDATE User u SET u.photoCnt = u.photoCnt + :count WHERE u.id = :userId")
    int incrementPhotoCount(@Param("userId") Long userId, @Param("count") int count);

    @Modifying(flushAutomatically = true)
    @Query("UPDATE User u SET u.photoCnt = u.photoCnt - :count WHERE u.id = :userId AND u.photoCnt >= :count")
    int decrementPhotoCount(@Param("userId") Long userId, @Param("count") int count);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        UPDATE User u
        SET u.albumCnt = u.albumCnt + 1
        WHERE u.id = :userId
    """)
    void incrementAlbumCnt(@Param("userId") Long userId);

    @Modifying
    @Query("""
        UPDATE User u
        SET u.albumCnt = u.albumCnt - 1
        WHERE u.id = :userId
        AND u.albumCnt > 0
    """)
    int decrementAlbumCnt(@Param("userId") Long userId);

    @Modifying(flushAutomatically = true)
    @Query("""
        UPDATE User u
        SET u.likesCnt = u.likesCnt + 1
        WHERE u.id = :userId
    """)
    void incrementLikeCnt(@Param("userId") Long userId);

    @Modifying(flushAutomatically = true)
    @Query("""
        UPDATE User u
        SET u.likesCnt = u.likesCnt - 1
        WHERE u.id = :userId
        AND u.likesCnt > 0
    """)
    void decrementLikeCnt(@Param("userId") Long userId);

    @Modifying
    @Query("""
        UPDATE User u
        SET u.likesCnt = u.likesCnt - :count
        WHERE u.id = :userId
        AND u.likesCnt >= :count
    """)
    int decrementLikeCntBy(
            @Param("userId") Long userId,
            @Param("count") int count
    );
}
