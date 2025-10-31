package com.cheeeese.album.domain;

import com.cheeeese.album.domain.type.Role;
import com.cheeeese.global.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "user_album", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "album_id"}))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserAlbum extends BaseEntity {

    @Id
    @Column(name = "user_album_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // TODO: 추후 필요시 ManyToOne, JoinColumn 넣을 예정
    @Column(name = "user_id", nullable = false)
    private Long userId;

    // TODO: 추후 필요시 ManyToOne, JoinColumn 넣을 예정
    @Column(name = "album_id", nullable = false)
    private Long albumId;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    @Column(name = "is_visible", nullable = false)
    private boolean isVisible;

    @Builder
    private UserAlbum(Long userId, Long albumId, Role role, boolean isVisible) {
        this.userId = userId;
        this.albumId = albumId;
        this.role = role;
        this.isVisible = isVisible;
    }

    public void hide() {
        this.isVisible = false;
    }

    public void show() {
        this.isVisible = true;
    }
}