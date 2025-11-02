package com.cheeeese.user.infrastructure.persistence;

import com.cheeeese.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByProviderId(String providerId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE User u SET u.photoCnt = u.photoCnt + :count WHERE u.id = :userId")
    int incrementPhotoCount(@Param("userId") Long userId, @Param("count") int count);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE User u SET u.photoCnt = u.photoCnt - :count WHERE u.id = :userId")
    int decrementPhotoCount(@Param("userId") Long userId, @Param("count") int count);
}
