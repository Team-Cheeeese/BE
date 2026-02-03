package com.cheeeese.global.common.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode implements BaseCode {

    // 공통 에러
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다."),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증이 필요합니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),

    CLOVA_API_ERROR(HttpStatus.BAD_GATEWAY, "CLOVA API 호출 중 오류가 발생했습니다."),
    CLOVA_RESPONSE_EMPTY(HttpStatus.BAD_GATEWAY, "CLOVA API로부터 빈 응답을 받았습니다."),
    AI_PARSING_FAILED(HttpStatus.UNPROCESSABLE_ENTITY, "AI 응답 파싱에 실패했습니다.")
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
