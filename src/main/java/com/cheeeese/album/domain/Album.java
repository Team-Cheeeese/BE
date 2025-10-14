package com.cheeeese.album.domain;

import com.cheeeese.album.exception.AlbumException;
import com.cheeeese.album.exception.code.AlbumErrorCode;
import com.cheeeese.global.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "album")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Album extends BaseEntity {

    @Id
    @Column(name = "album_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "host_id", nullable = false)
    private Long hostId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "code", nullable = false, unique = true)
    private String code;

    @Column(name = "theme_image_url")
    private String themeImageUrl;

    @Column(name = "participant", nullable = false)
    private int participant;

    @Column(name = "current_participant", nullable = false)
    private int currentParticipant;

    @Column(name = "event_date", nullable = false)
    private LocalDate eventDate;

    @Column(name = "max_photo_count", nullable = false)
    private int maxPhotoCount;

    @Column(name = "current_photo_count", nullable = false)
    private int currentPhotoCount;

    @Column(name = "is_info_available", nullable = false)
    private boolean isInfoAvailable;

    @Column(name = "expired_at", nullable = false)
    private LocalDateTime expiredAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AlbumStatus status;

    public enum AlbumStatus {
        ACTIVE, EXPIRED, DELETED
    }

    public boolean isExpired() {
        return this.expiredAt.isBefore(LocalDateTime.now()) || this.status == AlbumStatus.EXPIRED;
    }

    public boolean isFull() {
        return this.currentParticipant >= this.participant;
    }

    public void incrementParticipantCount() {
        if (isFull()) {
            throw new AlbumException(AlbumErrorCode.ALBUM_MAX_PARTICIPANT_REACHED);
        }
        this.currentParticipant++;
    }

    @Builder
    public Album(
            Long hostId,
            String title,
            String code,
            String themeImageUrl,
            int participant,
            int currentParticipant,
            LocalDate eventDate,
            int maxPhotoCount,
            int currentPhotoCount,
            boolean isInfoAvailable,
            LocalDateTime expiredAt,
            AlbumStatus status
    ) {
        this.hostId = hostId;
        this.title = title;
        this.code = code;
        this.themeImageUrl = themeImageUrl;
        this.participant = participant;
        this.currentParticipant = currentParticipant;
        this.eventDate = eventDate;
        this.maxPhotoCount = maxPhotoCount;
        this.currentPhotoCount = currentPhotoCount;
        this.isInfoAvailable = isInfoAvailable;
        this.expiredAt = expiredAt;
        this.status = status;
    }
}
