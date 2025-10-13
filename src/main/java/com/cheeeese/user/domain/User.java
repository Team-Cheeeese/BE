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

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "profile_image")
    private String profileImage;

    @Column(name = "provider_id", nullable = false)
    private String providerId;

    @Column(name = "is_service_agreement")
    private Boolean isServiceAgreement;

    @Column(name = "is_user_info_agreement")
    private Boolean isUserInfoAgreement;

    @Column(name = "is_marketing_agreement")
    private Boolean isMarketingAgreement;

    @Column(name = "is_third_party_agreement")
    private Boolean isThirdPartyAgreement;

    @Column(name = "album_cnt")
    private int albumCnt;

    @Column(name = "photo_cnt")
    private int photoCnt;

    @Column(name = "likes_cnt")
    private int likesCnt;

    @Builder
    private User(
            String name,
            String email,
            String profileImage,
            String providerId,
            Boolean isServiceAgreement,
            Boolean isUserInfoAgreement,
            Boolean isMarketingAgreement,
            Boolean isThirdPartyAgreement,
            int albumCnt,
            int photoCnt,
            int likesCnt
    ) {
        this.name = name;
        this.email = email;
        this.profileImage = profileImage;
        this.providerId = providerId;
        this.isServiceAgreement = isServiceAgreement;
        this.isUserInfoAgreement = isUserInfoAgreement;
        this.isMarketingAgreement = isMarketingAgreement;
        this.isThirdPartyAgreement = isThirdPartyAgreement;
        this.albumCnt = albumCnt;
        this.photoCnt = photoCnt;
        this.likesCnt = likesCnt;
    }

    public void saveUserOnboarding(String name) {
        this.name = name;
    }

    public void saveUserAgreement(
            boolean isServiceAgreement,
            boolean isUserInfoAgreement,
            boolean isMarketingAgreement,
            boolean isThirdPartyAgreement
    ) {
        this.isServiceAgreement = isServiceAgreement;
        this.isUserInfoAgreement = isUserInfoAgreement;
        this.isMarketingAgreement = isMarketingAgreement;
        this.isThirdPartyAgreement = isThirdPartyAgreement;
    }
}
