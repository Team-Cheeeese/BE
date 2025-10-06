package com.cheeeese.auth.application;

import com.cheeeese.auth.dto.response.TempCodeExchangeResponse;
import com.cheeeese.auth.exception.AuthException;
import com.cheeeese.auth.exception.code.AuthErrorCode;
import com.cheeeese.auth.infrastructure.mapper.AuthMapper;
import com.cheeeese.global.security.jwt.JwtProvider;
import com.cheeeese.global.util.RedisUtil;
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
    private final ObjectMapper objectMapper;
    private final RedisUtil redisUtil;

    public TempCodeExchangeResponse exchangeTempCode(String code) {
        Map<String, String> tokens = getTokenFromTempCode(code);
        User user = getUserFromToken(tokens.get("accessToken"));

        redisUtil.deleteValue("auth:" + code);

        return AuthMapper.toResponse(tokens.get("accessToken"), tokens.get("refreshToken"), user);
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

    private User getUserFromToken(String accessToken) {
        Claims claims = jwtProvider.getClaims(accessToken);
        String userId = claims.getSubject();

        return userRepository.findById(Long.valueOf(userId))
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
    }
}
