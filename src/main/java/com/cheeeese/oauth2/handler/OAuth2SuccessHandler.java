package com.cheeeese.oauth2.handler;

import com.cheeeese.global.security.CustomUserDetails;
import com.cheeeese.global.security.jwt.JwtProvider;
import com.cheeeese.global.util.RedisUtil;
import com.cheeeese.oauth2.infrastructure.mapper.RefreshTokenMapper;
import com.cheeeese.oauth2.infrastructure.persistence.RefreshTokenRepository;
import com.cheeeese.user.domain.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final ObjectMapper objectMapper;
    private final RedisUtil redisUtil;

    @Value("${frontend.oauth2.redirect-uri}")
    private String redirectUri;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = customUserDetails.getUser();

        String accessToken = jwtProvider.createAccessToken(user.getId());
        String refreshToken = jwtProvider.createRefreshToken(user.getId());

        refreshTokenRepository.save(RefreshTokenMapper.toRefreshToken(user, refreshToken));

        String tempCode = UUID.randomUUID().toString();

        redisUtil.setValue("auth:" + tempCode,
                objectMapper.writeValueAsString(Map.of(
                        "accessToken", accessToken,
                        "refreshToken", refreshToken
                )),
                1000 * 60L
        );

        String callbackUri = UriComponentsBuilder
                .fromUriString(redirectUri)
                .queryParam("code", tempCode)
                .build()
                .toUriString();

        response.sendRedirect(callbackUri);
    }
}
