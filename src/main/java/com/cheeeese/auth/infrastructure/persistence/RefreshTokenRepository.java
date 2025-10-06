package com.cheeeese.auth.infrastructure.persistence;

import com.cheeeese.auth.domain.RefreshToken;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByUserId(Long userId);
}
