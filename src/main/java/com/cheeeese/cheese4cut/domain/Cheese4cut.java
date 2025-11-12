package com.cheeeese.cheese4cut.domain;

import com.cheeeese.album.domain.Album;
import com.cheeeese.global.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
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

    @OneToMany(mappedBy = "cheese4cut", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("photoRank ASC")
    private List<Cheese4cutPhoto> photos = new ArrayList<>();


    @Builder
    private Cheese4cut(Album album, List<Cheese4cutPhoto> photos) {
        this.album = album;
        if (photos != null) {
            photos.forEach(this::addPhoto);
        }
    }

    private void addPhoto(Cheese4cutPhoto photo) {
        photo.assignToCheese4cut(this);
        this.photos.add(photo);
    }
}

