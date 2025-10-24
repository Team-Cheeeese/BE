package com.cheeeese.photo.exception.code;

import com.cheeeese.global.common.code.BaseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum PhotoErrorCode implements BaseCode {

    PHOTO_MAX_COUNT_EXCEEDED(HttpStatus.BAD_REQUEST, "앨범의 최대 사진 개수를 초과합니다."),
    PHOTO_FILE_SIZE_EXCEEDED(HttpStatus.BAD_REQUEST, "파일 크기는 6MB를 초과할 수 없습니다."),
    PHOTO_INVALID_CONTENT_TYPE(HttpStatus.BAD_REQUEST, "유효한 이미지 파일 형식이 아닙니다."),
    PHOTO_FILE_LIST_EMPTY(HttpStatus.BAD_REQUEST, "업로드할 파일 목록이 비어 있습니다."),
    PHOTO_FILE_NAME_REQUIRED(HttpStatus.BAD_REQUEST, "파일명이 누락되었습니다."),
    ;
    ;

    private final HttpStatus httpStatus;
    private final String message;
}

