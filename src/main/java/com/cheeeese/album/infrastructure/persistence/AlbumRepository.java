package com.cheeeese.album.infrastructure.persistence;

import com.cheeeese.album.domain.Album;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AlbumRepository extends JpaRepository<Album, Long> {
    Optional<Album> findByCode(String code);
}
