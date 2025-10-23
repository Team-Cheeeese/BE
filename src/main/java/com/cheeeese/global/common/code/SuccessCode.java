package com.cheeeese.global.common.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SuccessCode implements BaseCode {

    // health_check
    HEALTH_CHECK_SUCCESS(HttpStatus.OK, "🧀 치이이즈 서버가 정상적으로 작동 중입니다."),

    // auth
    TOKEN_EXCHANGE_SUCCESS(HttpStatus.OK, "토큰 교환이 성공적으로 완료되었습니다."),
    TOKEN_REISSUE_SUCCESS(HttpStatus.OK, "토큰 재발급이 성공적으로 완료되었습니다."),
    LOGOUT_SUCCESS(HttpStatus.OK, "로그아웃이 성공적으로 완료되었습니다."),

    // user
    USER_PROFILE_UPDATE_SUCCESS(HttpStatus.OK, "사용자 프로필 업데이트가 성공적으로 완료되었습니다."),
    USER_AGREEMENT_ACCEPT_SUCCESS(HttpStatus.OK, "사용자 이용 약관 동의가 성공적으로 완료되었습니다."),

    // album
    ALBUM_INVITATION_FETCH_SUCCESS(HttpStatus.OK, "앨범 초대장 정보 조회가 성공적으로 완료되었습니다."),
    ALBUM_ENTER_SUCCESS(HttpStatus.OK, "앨범 입장이 성공적으로 완료되었습니다."),
    ALBUM_CREATE_SUCCESS(HttpStatus.OK, "앨범 생성이 성공적으로 완료되었습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
