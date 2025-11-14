package com.cheeeese.cheese4cut.domain;

import com.cheeeese.global.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "cheese4cut_photo")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Cheese4cutPhoto extends BaseEntity {

    @Id
    @Column(name = "cheese4cut_photo_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cheese4cut_id", nullable = false)
    private Cheese4cut cheese4cut;

    @Column(name = "photo_id", nullable = false)
    private Long photoId;

    @Column(name = "image_url", columnDefinition = "TEXT", nullable = false)
    private String imageUrl;

    @Column(name = "thumbnail_image_url", columnDefinition = "TEXT")
    private String thumbnailImageUrl;

    @Column(name = "photo_rank", nullable = false)
    private int photoRank;

    @Builder
    private Cheese4cutPhoto(Long photoId, String imageUrl, String thumbnailImageUrl, int photoRank) {
        this.photoId = photoId;
        this.imageUrl = imageUrl;
        this.thumbnailImageUrl = thumbnailImageUrl;
        this.photoRank = photoRank;
    }

    void assignToCheese4cut(Cheese4cut cheese4cut) {
        this.cheese4cut = cheese4cut;
    }
}
