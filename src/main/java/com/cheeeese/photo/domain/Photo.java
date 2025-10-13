package com.cheeeese.photo.domain;

import com.cheeeese.global.domain.BaseEntity;
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

    // TODO: 추후 필요시 ManyToOne, JoinColumn 넣을 예정
    @Column(name = "user_id", nullable = false)
    private Long userId;

    // TODO: 추후 필요시 ManyToOne, JoinColumn 넣을 예정
    @Column(name = "album_id", nullable = false)
    private Long albumId;

    @Column(name = "image_url", nullable = false, columnDefinition = "TEXT")
    private String imageUrl;

    @Column(name = "likes_cnt", nullable = false)
    private int likesCnt;

    @Column(name = "capture_time", nullable = false)
    private LocalDateTime captureTime;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;

    @Builder
    public Photo(
            Long userId,
            Long albumId,
            String imageUrl,
            LocalDateTime captureTime
    ) {
        this.userId = userId;
        this.albumId = albumId;
        this.imageUrl = imageUrl;
        this.captureTime = captureTime;
        this.likesCnt = 0;
        this.isDeleted = false;
    }
}
