package com.cheeeese.album.exception;

import com.cheeeese.album.exception.code.AlbumErrorCode;
import com.cheeeese.global.exception.BusinessException;
import lombok.Getter;

@Getter
public class AlbumException extends BusinessException {
    public AlbumException(AlbumErrorCode errorCode) {
        super(errorCode);
    }
}
