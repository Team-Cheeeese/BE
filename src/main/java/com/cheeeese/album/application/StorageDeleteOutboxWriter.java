package com.cheeeese.album.application;

import com.cheeeese.album.domain.StorageDeleteOutbox;
import com.cheeeese.album.infrastructure.persistence.StorageDeleteOutboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StorageDeleteOutboxWriter {

    private final StorageDeleteOutboxRepository outboxRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void save(Long albumId, String payloadJson, String reason) {
        outboxRepository.save(StorageDeleteOutbox.of(albumId, payloadJson, reason));
    }
}
