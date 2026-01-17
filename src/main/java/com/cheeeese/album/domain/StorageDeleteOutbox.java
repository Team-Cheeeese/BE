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

    @Column(name = "album_id", nullable = false)
    private Long albumId;

    @Column(name = "payload_json", nullable = false, columnDefinition = "LONGTEXT")
    @Lob
    private String payloadJson;

    @Column(nullable = false)
    private String reason;

    @Column(name = "created_at", nullable = false)
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
