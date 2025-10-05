package com.cheeeese.oauth2.application;

import com.cheeeese.global.security.CustomUserDetails;
import com.cheeeese.oauth2.infrastructure.userinfo.KakaoUserInfo;
import com.cheeeese.user.domain.User;
import com.cheeeese.user.infrastructure.mapper.UserMapper;
import com.cheeeese.user.infrastructure.persistence.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        Map<String, Object> attributes = oAuth2User.getAttributes();

        KakaoUserInfo userInfo = extractKakaoUserInfo(attributes);

        User user = userRepository.findByEmail(userInfo.getEmail())
                .orElseGet(() -> {
                    User newUser = UserMapper.toEntity(userInfo);
                    return userRepository.save(newUser);
                });

        return new CustomUserDetails(user, attributes);
    }

    private KakaoUserInfo extractKakaoUserInfo(Map<String, Object> attributes) {
        return new KakaoUserInfo(attributes);
    }
}
