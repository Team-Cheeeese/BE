package com.cheeeese.global.common.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SuccessCode implements BaseCode {

    // health_check
    HEALTH_CHECK_SUCCESS(HttpStatus.OK, "Health Check Success"),

    // auth
    TOKEN_EXCHANGE_SUCCESS(HttpStatus.OK, "Token Exchange Success"),
    TOKEN_REISSUE_SUCCESS(HttpStatus.OK, "Token Reissue Success"),
    LOGOUT_SUCCESS(HttpStatus.OK, "Logout Success"),

    // user
    USER_ONBOARDING_SUCCESS(HttpStatus.OK, "User Onboarding Success"),
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
