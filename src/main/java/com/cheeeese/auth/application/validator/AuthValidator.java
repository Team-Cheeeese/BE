package com.cheeeese.auth.application.validator;

import com.cheeeese.auth.domain.RefreshToken;
import com.cheeeese.auth.exception.AuthException;
import com.cheeeese.auth.exception.code.AuthErrorCode;
import com.cheeeese.auth.infrastructure.persistence.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthValidator {

    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshToken getRefreshTokenByUserId(Long userId) {
        return refreshTokenRepository.findByUserId(userId)
                .orElseThrow(() -> new AuthException(AuthErrorCode.REFRESH_TOKEN_NOT_FOUND));
    }

    public RefreshToken validateRefreshToken(Long userId, String refreshToken) {
        RefreshToken savedToken = getRefreshTokenByUserId(userId);

        if (!savedToken.getRefreshToken().equals(refreshToken)) {
            throw new AuthException(AuthErrorCode.INVALID_TOKEN);
        }
        return savedToken;
    }
}
