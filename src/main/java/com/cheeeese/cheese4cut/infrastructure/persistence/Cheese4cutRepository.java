package com.cheeeese.cheese4cut.infrastructure.persistence;

import com.cheeeese.cheese4cut.domain.Cheese4cut;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface Cheese4cutRepository extends JpaRepository<Cheese4cut, Long> {
    Optional<Cheese4cut> findByAlbumId(Long albumId);
}
