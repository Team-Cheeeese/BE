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
    USER_PROFILE_UPDATE_SUCCESS(HttpStatus.OK, "User Profile Update Success"),
    USER_AGREEMENT_ACCEPT_SUCCESS(HttpStatus.OK, "User Agreement Accept Success"),

    // album
    ALBUM_INVITATION_FETCH_SUCCESS(HttpStatus.OK, "앨범 초대장 정보 조회가 성공적으로 완료되었습니다."),
    ALBUM_ENTER_SUCCESS(HttpStatus.OK, "앨범 입장이 성공적으로 완료되었습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
