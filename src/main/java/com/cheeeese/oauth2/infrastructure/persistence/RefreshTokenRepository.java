package com.cheeeese.oauth2.infrastructure.persistence;

import com.cheeeese.oauth2.domain.RefreshToken;
import org.springframework.data.repository.CrudRepository;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, Long> {
}
