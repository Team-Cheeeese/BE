package com.cheeeese.auth.application;

import com.cheeeese.auth.dto.request.AuthReissueRequest;
import com.cheeeese.auth.dto.response.AuthReissueResponse;
import com.cheeeese.auth.dto.response.AuthExchangeResponse;
import com.cheeeese.auth.exception.AuthException;
import com.cheeeese.auth.exception.code.AuthErrorCode;
import com.cheeeese.auth.infrastructure.mapper.AuthMapper;
import com.cheeeese.global.security.jwt.JwtProvider;
import com.cheeeese.global.util.RedisUtil;
import com.cheeeese.oauth2.domain.RefreshToken;
import com.cheeeese.oauth2.infrastructure.persistence.RefreshTokenRepository;
import com.cheeeese.user.domain.User;
import com.cheeeese.user.exception.UserException;
import com.cheeeese.user.exception.code.UserErrorCode;
import com.cheeeese.user.infrastructure.persistence.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final ObjectMapper objectMapper;
    private final RedisUtil redisUtil;

    public AuthExchangeResponse exchangeTempCode(String code) {
        Map<String, String> tokens = getTokenFromTempCode(code);
        User user = getUserFromToken(tokens.get("accessToken"));

        redisUtil.deleteValue("auth:" + code);

        return AuthMapper.toResponse(tokens.get("accessToken"), tokens.get("refreshToken"), user);
    }

    @Transactional
    public AuthReissueResponse reissueToken(AuthReissueRequest request) {
        jwtProvider.validateToken(request.refreshToken());

        User user = getUserFromToken(request.refreshToken());

        RefreshToken savedToken = refreshTokenRepository.findByUserId(user.getId())
                .orElseThrow(() -> new AuthException(AuthErrorCode.REFRESH_TOKEN_NOT_FOUND));

        String newAccessToken = jwtProvider.createAccessToken(user.getId());
        String newRefreshToken = jwtProvider.createRefreshToken(user.getId());

        savedToken.updateRefreshToken(newRefreshToken);
        refreshTokenRepository.save(savedToken);

        return AuthMapper.toResponse(newAccessToken, newRefreshToken);
    }

    private Map<String, String> getTokenFromTempCode(String code) {
        String key = "auth:" + code;
        String json = redisUtil.getValue(key);

        if (json == null) {
            throw new AuthException(AuthErrorCode.INVALID_AUTH_CODE);
        }

        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            throw new AuthException(AuthErrorCode.TOKEN_PARSE_FAILED);
        }
    }

    private User getUserFromToken(String token) {
        Claims claims = jwtProvider.getClaims(token);
        String userId = claims.getSubject();

        return userRepository.findById(Long.valueOf(userId))
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
    }
}
