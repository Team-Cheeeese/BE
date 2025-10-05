package com.cheeeese.auth.exception;

import com.cheeeese.auth.exception.code.AuthErrorCode;
import com.cheeeese.global.exception.BusinessException;
import lombok.Getter;

@Getter
public class AuthException extends BusinessException {
    public AuthException(AuthErrorCode errorCode) {
        super(errorCode);
    }
}
