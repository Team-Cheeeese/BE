package com.cheeeese.cheese4cut.domain;

import com.cheeeese.album.domain.Album;
import com.cheeeese.global.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Getter
@Table(name = "cheese4cut")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Cheese4cut extends BaseEntity {

    @Id
    @Column(name = "cheese4cut_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "album_id", nullable = false, unique = true)
    private Album album;

    @ElementCollection
    @CollectionTable(name = "cheese4cut_photos", joinColumns = @JoinColumn(name = "cheese4cut_id"))
    @Column(name = "photo_id", nullable = false)
    private List<Long> photoIds;

    @Column(name = "frame_image_url", nullable = false)
    private String frameImageUrl;

    @Builder
    private Cheese4cut(Album album, List<Long> photoIds, String frameImageUrl) {
        this.album = album;
        this.photoIds = photoIds;
        this.frameImageUrl = frameImageUrl;
    }
}

