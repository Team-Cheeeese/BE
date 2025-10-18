package com.cheeeese.album.infrastructure.persistence;

import com.cheeeese.album.domain.AlbumParticipant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AlbumParticipantRepository extends JpaRepository<AlbumParticipant, Long> {
    Optional<AlbumParticipant> findByUserIdAndAlbumId(Long userId, Long albumId);

    List<AlbumParticipant> findAllByAlbumId(Long albumId);

    Optional<AlbumParticipant> findByAlbumIdAndUserIdAndIsBlacklistedTrue(Long albumId, Long userId);
}
