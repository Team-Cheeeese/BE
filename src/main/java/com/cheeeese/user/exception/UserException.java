package com.cheeeese.user.exception;

import com.cheeeese.global.exception.BusinessException;
import com.cheeeese.user.exception.code.UserErrorCode;
import lombok.Getter;

@Getter
public class UserException extends BusinessException {
    public UserException(UserErrorCode errorCode) {
        super(errorCode);
    }
}
