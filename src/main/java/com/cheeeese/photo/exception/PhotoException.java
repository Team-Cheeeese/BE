package com.cheeeese.photo.exception;

import com.cheeeese.global.exception.BusinessException;
import com.cheeeese.photo.exception.code.PhotoErrorCode;
import lombok.Getter;

@Getter
public class PhotoException extends BusinessException {
    public PhotoException(PhotoErrorCode errorCode) {
        super(errorCode);
    }
}

