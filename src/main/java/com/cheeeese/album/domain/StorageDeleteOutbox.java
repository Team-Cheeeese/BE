package com.cheeeese.album.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StorageDeleteOutbox {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long albumId;

    @Lob
    @Column(nullable = false)
    private String payloadJson;

    @Column(nullable = false)
    private String reason;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private StorageDeleteOutbox(Long albumId, String payloadJson, String reason) {
        this.albumId = albumId;
        this.payloadJson = payloadJson;
        this.reason = reason;
        this.createdAt = LocalDateTime.now();
    }

    public static StorageDeleteOutbox of(Long albumId, String payloadJson, String reason) {
        return new StorageDeleteOutbox(albumId, payloadJson, reason);
    }
}
