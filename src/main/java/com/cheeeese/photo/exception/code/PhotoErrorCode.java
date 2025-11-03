package com.cheeeese.photo.exception.code;

import com.cheeeese.global.common.code.BaseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum PhotoErrorCode implements BaseCode {

    // Presigned URL 발급 관련 오류
    PHOTO_MAX_COUNT_EXCEEDED(HttpStatus.BAD_REQUEST, "앨범의 최대 사진 개수를 초과합니다."),
    PHOTO_FILE_SIZE_EXCEEDED(HttpStatus.BAD_REQUEST, "파일 크기는 6MB를 초과할 수 없습니다."),
    PHOTO_INVALID_CONTENT_TYPE(HttpStatus.BAD_REQUEST, "유효한 이미지 파일 형식이 아닙니다."),
    PHOTO_FILE_LIST_EMPTY(HttpStatus.BAD_REQUEST, "업로드할 파일 목록이 비어 있습니다."),
    PHOTO_FILE_NAME_REQUIRED(HttpStatus.BAD_REQUEST, "파일명이 누락되었습니다."),
    PHOTO_COUNT_INCREMENT_FAILED(HttpStatus.CONFLICT, "앨범 사진 개수 증가에 실패했습니다."),

    // 업로드 보고 관련 오류
    PHOTO_ID_LIST_EMPTY(HttpStatus.BAD_REQUEST, "요청에 photoId 리스트가 비어 있습니다."),
    PHOTO_REPORT_INVALID_ALBUM(HttpStatus.BAD_REQUEST, "보고된 사진들은 반드시 동일한 앨범에 속해야 합니다."),
    PHOTO_OWNER_MISMATCH(HttpStatus.FORBIDDEN, "사용자와 사진의 소유자가 일치하지 않습니다."),
    PHOTO_ID_NOT_FOUND(HttpStatus.NOT_FOUND, "보고된 사진 ID 중 존재하지 않는 ID가 포함되어 있습니다."),
    PHOTO_REPORT_CONFLICTING_IDS(HttpStatus.BAD_REQUEST, "업로드 결과(success/failure) 목록에 중복된 사진 ID가 포함되어 있습니다."),
    PHOTO_STATUS_UPDATE_FAILED(HttpStatus.CONFLICT, "사진 상태 업데이트에 실패했습니다."),
    PHOTO_COUNT_DECREMENT_FAILED(HttpStatus.CONFLICT, "앨범 사진 개수 감소에 실패했습니다."),
    THUMBNAIL_UPDATE_FAILED(HttpStatus.CONFLICT, "썸네일 상태 업데이트에 실패했습니다."),

    // 사진 도메인 관련 오류
    PHOTO_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 사진을 찾을 수 없습니다."),
    PHOTO_LIKES_NOT_FOUND(HttpStatus.NOT_FOUND, "사진에 대한 좋아요 내역이 존재하지 않습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;
}

