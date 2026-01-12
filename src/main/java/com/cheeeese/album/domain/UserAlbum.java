package com.cheeeese.album.domain;

import com.cheeeese.album.domain.type.Role;
import com.cheeeese.global.domain.BaseEntity;
import com.cheeeese.user.domain.User;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "album_id", nullable = false)
    private Album album;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    @Column(name = "is_visible", nullable = false)
    private boolean isVisible;

    @Builder
    private UserAlbum(User user, Album album, Role role, boolean isVisible) {
        this.user = user;
        this.album = album;
        this.role = role;
        this.isVisible = isVisible;
    }

    public void hide() {
        this.isVisible = false;
    }

    public void show() {
        this.isVisible = true;
    }

    public void blacklist() {
        hide();
        this.role = Role.BLACK;
    }

    public boolean isBlacklisted() {
        return this.role == Role.BLACK;
    }
}