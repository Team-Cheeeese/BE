package com.cheeeese.auth.application;

import com.cheeeese.auth.application.validator.AuthValidator;
import com.cheeeese.auth.dto.request.AuthReissueRequest;
import com.cheeeese.auth.dto.response.AuthReissueResponse;
import com.cheeeese.auth.dto.response.AuthExchangeResponse;
import com.cheeeese.auth.exception.AuthException;
import com.cheeeese.auth.exception.code.AuthErrorCode;
import com.cheeeese.auth.infrastructure.mapper.AuthMapper;
import com.cheeeese.global.security.jwt.JwtProvider;
import com.cheeeese.global.util.RedisUtil;
import com.cheeeese.auth.domain.RefreshToken;
import com.cheeeese.auth.infrastructure.persistence.RefreshTokenRepository;
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

import java.time.Duration;
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
    private final AuthValidator authValidator;
    private final TokenBlacklistService tokenBlacklistService;

    public AuthExchangeResponse exchangeTempCode(String code) {
        Map<String, String> tokens = getTokenFromTempCode(code);
        User user = getUserFromToken(tokens.get("accessToken"));

        redisUtil.deleteValue("auth:" + code);

        return AuthMapper.toExchangeResponse(tokens.get("accessToken"), tokens.get("refreshToken"), user);
    }

    @Transactional
    public AuthReissueResponse reissueToken(AuthReissueRequest request) {
        jwtProvider.validateToken(request.refreshToken());

        User user = getUserFromToken(request.refreshToken());

        RefreshToken savedToken = authValidator.validateRefreshToken(user.getId(), request.refreshToken());

        String newAccessToken = jwtProvider.createAccessToken(user.getId());
        String newRefreshToken = jwtProvider.createRefreshToken(user.getId());

        savedToken.updateRefreshToken(newRefreshToken);
        refreshTokenRepository.save(savedToken);

        return AuthMapper.toReissueResponse(newAccessToken, newRefreshToken);
    }

    @Transactional
    public void logout(String accessToken) {
        User user = getUserFromToken(accessToken);
        Claims claims = jwtProvider.getClaims(accessToken);

        RefreshToken refreshToken = refreshTokenRepository.findByUserId(user.getId())
                .orElseThrow(() -> new AuthException(AuthErrorCode.REFRESH_TOKEN_NOT_FOUND));

        refreshTokenRepository.delete(refreshToken);

        long expiration = claims.getExpiration().getTime() - System.currentTimeMillis();

        if (expiration <= 0) {
            expiration = 1000;
        }
        tokenBlacklistService.addBlackList(accessToken, "logout", Duration.ofMillis(expiration));
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
            redisUtil.deleteValue(key);
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
