package com.cheeeese.oauth2.infrastructure.userinfo;

import com.cheeeese.oauth2.domain.OAuth2UserInfo;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public class KakaoUserInfo implements OAuth2UserInfo {

    private final Map<String, Object> attributes;

    @Override
    public String getProviderId() {
        return String.valueOf(attributes.get("id"));
    }

    @Override
    public String getEmail() {
        Map<String, Object> accountMap = (Map<String, Object>) attributes.get("kakao_account");
        if (accountMap == null) {
            return null;
        }
        return (String) accountMap.get("email");
    }

    @Override
    public String getName() {
        Map<String, Object> accountMap = (Map<String, Object>) attributes.get("kakao_account");
        if (accountMap == null) {
            return null;
        }
        Map<String, Object> profileMap = (Map<String, Object>) accountMap.get("profile");
        if (profileMap == null) {
            return null;
        }
        return (String) profileMap.get("nickname");
    }

    @Override
    public String getProfileImage() {
        Map<String, Object> accountMap = (Map<String, Object>) attributes.get("kakao_account");
        if (accountMap == null) {
            return null;
        }
        Map<String, Object> profileMap = (Map<String, Object>) accountMap.get("profile");
        if (profileMap == null) {
            return null;
        }
        return (String) profileMap.get("profile_image_url");
    }
}
