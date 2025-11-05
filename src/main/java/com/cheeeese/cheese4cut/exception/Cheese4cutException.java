package com.cheeeese.cheese4cut.exception;

import com.cheeeese.cheese4cut.exception.code.Cheese4cutErrorCode;
import com.cheeeese.global.exception.BusinessException;
import lombok.Getter;

@Getter
public class Cheese4cutException extends BusinessException {
    public Cheese4cutException(Cheese4cutErrorCode errorCode) {
        super(errorCode);
    }
}
