package com.cheeeese.album.infrastructure.persistence;

import com.cheeeese.album.domain.StorageDeleteOutbox;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StorageDeleteOutboxRepository extends JpaRepository<StorageDeleteOutbox, Long> {
}
