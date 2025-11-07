package com.cheeeese.cheese4cut.exception.code;

import com.cheeeese.global.common.code.BaseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum Cheese4cutErrorCode implements BaseCode {

    CHEESE4CUT_NOT_FOUND(HttpStatus.NOT_FOUND, "치즈네컷이 존재하지 않습니다."),
    INSUFFICIENT_COUNT_FOR_CHEESE4CUT(HttpStatus.BAD_REQUEST, "치즈네컷 생성을 위한 완료된 사진이 4장 미만입니다."),
    CHEESE4CUT_ALREADY_FINALIZED(HttpStatus.CONFLICT, "치즈네컷이 이미 확정되었습니다."),
    CHEESE4CUT_INVALID_PHOTO_COUNT(HttpStatus.BAD_REQUEST, "치즈네컷 확정 시 4장의 사진 ID만 허용됩니다."),
    CHEESE4CUT_PHOTO_INVALID_STATUS_OR_ALBUM(HttpStatus.BAD_REQUEST, "확정하려는 사진은 해당 앨범에 속하며, 업로드가 완료(COMPLETED)된 상태여야 합니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;
}