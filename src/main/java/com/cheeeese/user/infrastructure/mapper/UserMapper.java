package com.cheeeese.user.infrastructure.mapper;

import com.cheeeese.oauth2.domain.OAuth2UserInfo;
import com.cheeeese.user.domain.User;
import com.cheeeese.user.dto.response.UserInfoResponse;
import com.cheeeese.user.domain.type.ProfileImageType;
import com.cheeeese.user.dto.response.UserProfileImageResponse;

import java.util.List;

public class UserMapper {

    private static final String DEFAULT_PROFILE_IMAGE = "P1";

    public static User toEntity(OAuth2UserInfo oAuth2UserInfo) {
        return User.builder()
                .email(oAuth2UserInfo.getEmail())
                .name(oAuth2UserInfo.getName())
                .profileImage(DEFAULT_PROFILE_IMAGE)
                .providerId(oAuth2UserInfo.getProviderId())
                .build();
    }

    public static UserInfoResponse toUserInfoResponse(User user) {
        return UserInfoResponse.builder()
                .profileImage(user.getProfileImage())
                .name(user.getName())
                .albumCount(user.getAlbumCnt())
                .photoCount(user.getPhotoCnt())
                .likesCount(user.getLikesCnt())
                .build();
    }

    public static UserProfileImageResponse.ProfileImageOpt toProfileImageOpt(ProfileImageType type, String imageUrl) {
        return UserProfileImageResponse.ProfileImageOpt.builder()
                .imageCode(type.name())
                .profileImageUrl(imageUrl)
                .build();
    }

    public static UserProfileImageResponse toProfileImageResponse(List<UserProfileImageResponse.ProfileImageOpt> opts) {
        return UserProfileImageResponse.builder()
                .opts(opts)
                .build();
    }
}
