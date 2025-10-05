package com.cheeeese.global.common.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SuccessCode implements BaseCode{

    // health_check
    HEALTH_CHECK_SUCCESS(HttpStatus.OK, "Health Check Success"),

    // auth
    EXCHANGE_TOKEN_SUCCESS(HttpStatus.OK, "Exchange Token Success"),
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
