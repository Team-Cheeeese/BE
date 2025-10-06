package com.cheeeese.oauth2.domain;

public interface OAuth2UserInfo {
    String getProviderId();
    String getEmail();
    String getName();
    String getProfileImage();
}
