package com.cheeeese.photo.domain;

import com.cheeeese.album.domain.Album;
import com.cheeeese.global.domain.BaseEntity;
import com.cheeeese.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "photo")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Photo extends BaseEntity {

    @Id
    @Column(name = "photo_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "album_id", nullable = false)
    private Album album;

    @Column(name = "image_url", columnDefinition = "TEXT")
    private String imageUrl;

    @Column(name = "thumbnail_url", columnDefinition = "TEXT")
    private String thumbnailUrl;

    @Column(name = "likes_cnt", nullable = false)
    private int likesCnt;

    @Column(name = "capture_time", nullable = false)
    private LocalDateTime captureTime;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PhotoStatus status;

    @Builder
    private Photo(
            User user,
            Album album,
            String imageUrl,
            String thumbnailUrl,
            LocalDateTime captureTime,
            PhotoStatus status
    ) {
        this.user = user;
        this.album = album;
        this.imageUrl = imageUrl;
        this.thumbnailUrl = thumbnailUrl;
        this.captureTime = captureTime;
        this.likesCnt = 0;
        this.isDeleted = false;
        this.status = status;
    }

    public void updateImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void softDelete() {
        this.isDeleted = true;
    }
}
