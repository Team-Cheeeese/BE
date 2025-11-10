package com.cheeeese.photo.domain;

import com.cheeeese.global.domain.BaseEntity;
import com.cheeeese.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(
        name = "photo_history",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"user_id", "photo_id"}
        )
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PhotoHistory extends BaseEntity {

    @Id
    @Column(name = "photo_history_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "photo_id", nullable = false)
    private Photo photo;

    @Builder
    private PhotoHistory(User user, Photo photo) {
        this.user = user;
        this.photo = photo;
    }

    public void touch() {
        this.markUpdated();
    }
}
