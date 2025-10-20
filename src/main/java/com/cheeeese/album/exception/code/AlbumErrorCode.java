package com.cheeeese.album.exception.code;

import com.cheeeese.global.common.code.BaseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AlbumErrorCode implements BaseCode {

    ALBUM_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않거나 유효하지 않은 앨범 코드입니다."),
    ALBUM_EXPIRED(HttpStatus.BAD_REQUEST, "만료된 앨범입니다."),
    ALBUM_MAX_PARTICIPANT_REACHED(HttpStatus.BAD_REQUEST, "앨범의 최대 참가 인원수를 초과했습니다."),
    USER_IS_BLACKLISTED(HttpStatus.FORBIDDEN, "앨범 관리자에 의해 접근이 금지된 사용자입니다."),
    USER_ALREADY_JOINED_CONCURRENTLY(HttpStatus.CONFLICT, "동시성 오류: 이미 앨범에 참여 요청이 완료되었습니다."),
    ALBUM_REQUIRED_TERMS_NOT_AGREED(HttpStatus.BAD_REQUEST, "앨범 생성 필수 약관에 동의하지 않았습니다."),
    ALBUM_THEME_IMAGE_NOT_SELECTED(HttpStatus.BAD_REQUEST, "앨범 썸네일 이미지가 선택되지 않았습니다."),
    ALBUM_EVENT_DATE_REQUIRED(HttpStatus.BAD_REQUEST, "행사 날짜가 입력되지 않았습니다."),
    ALBUM_EVENT_DATE_INVALID(HttpStatus.BAD_REQUEST, "행사 날짜는 오늘 또는 과거만 선택 가능합니다."),
    ALBUM_INVALID_CAPACITY(HttpStatus.BAD_REQUEST, "앨범 인원은 최소 1명 이상 최대 64명 이하여야 합니다."),
    ALBUM_CREATION_LIMIT_EXCEEDED(HttpStatus.CONFLICT, "사용자는 일주일에 최대 3개의 앨범만 생성할 수 있습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;
}