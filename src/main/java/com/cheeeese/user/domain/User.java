package com.cheeeese.user.domain;

import com.cheeeese.global.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @Id
    @Column(name = "user_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "profile_image")
    private String profileImage;

    @Column(name = "provider_id", nullable = false)
    private String providerId;

    @Builder
    private User(
            String name,
            String email,
            String profileImage,
            String providerId
    ) {
        this.name = name;
        this.email = email;
        this.profileImage = profileImage;
        this.providerId = providerId;
    }
}
