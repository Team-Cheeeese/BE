package com.cheeeese.cheese4cut.exception.code;

import com.cheeeese.global.common.code.BaseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum Cheese4cutErrorCode implements BaseCode {

    CHEESE4CUT_NOT_FOUND(HttpStatus.NOT_FOUND, "치즈네컷이 존재하지 않습니다."),

    ;

    private final HttpStatus httpStatus;
    private final String message;
}