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
    USER_ALREADY_JOINED_CONCURRENTLY(HttpStatus.BAD_REQUEST, "동시성 오류: 이미 앨범에 참여 요청이 완료되었습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;
}