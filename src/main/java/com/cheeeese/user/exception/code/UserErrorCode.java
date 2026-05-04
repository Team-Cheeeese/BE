package com.cheeeese.user.exception.code;

import com.cheeeese.global.common.code.BaseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements BaseCode {

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다."),
    USER_NAME_REQUIRED(HttpStatus.BAD_REQUEST, "사용자 이름은 필수 입력 값입니다."),
    USER_PROFILE_IMAGE_CODE_REQUIRED(HttpStatus.BAD_REQUEST, "프로필 이미지 코드는 필수 입력 값입니다."),
    REQUIRED_TERMS_NOT_AGREED(HttpStatus.BAD_REQUEST, "필수 약관에 동의하지 않았습니다."),
    USER_PHOTO_COUNT_INCREMENT_FAILED(HttpStatus.CONFLICT, "유저의 앨범 사진 개수 증가에 실패했습니다."),
    USER_PHOTO_COUNT_DECREMENT_FAILED(HttpStatus.CONFLICT, "유저의 앨범 사진 개수 감소에 실패했습니다."),
    USER_ALBUM_COUNT_DECREMENT_FAILED(HttpStatus.CONFLICT, "사용자의 앨범 개수 감소에 실패했습니다."),
    USER_PHOTO_LIKE_COUNT_DECREMENT_FAILED(HttpStatus.CONFLICT, "사용자의 띱 수 개수 감소에 실패했습니다."),
    USER_PROFILE_IMAGE_CODE_INVALID(HttpStatus.BAD_REQUEST, "유효하지 않은 프로필 이미지 코드입니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
