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
    ALBUM_PARTICIPANT_FETCH_SUCCESS(HttpStatus.OK, "앨범 참여자 조회가 성공적으로 완료되었습니다."),

    // photo
    PHOTO_AVAILABLE_COUNT_FETCH_SUCCESS(HttpStatus.OK, "업로드 가능 사진 수 조회가 성공적으로 완료되었습니다."),
    PRESIGNED_URL_ISSUE_SUCCESS(HttpStatus.OK, "Presigned URL 발급이 성공적으로 완료되었습니다."),
    PHOTO_UPLOAD_REPORT_SUCCESS(HttpStatus.OK, "사진 업로드 결과 보고가 성공적으로 처리되었습니다."),
    THUMBNAIL_PRODUCE_COMPLETE(HttpStatus.OK, "썸네일 생성이 성공적으로 완료되었습니다."),
    PHOTO_LIST_GET_SUCCESS(HttpStatus.OK, "앨범 내 사진 목록 조회가 성공적으로 완료되었습니다."),
    PHOTO_LIKES_LIST_GET_SUCCESS(HttpStatus.OK, "내가 띱한 사진 목록 조회가 성공적으로 완료되었습니다."),
    PHOTO_DETAIL_GET_SUCCESS(HttpStatus.OK, "앨범 내 사진 상세 조회가 성공적으로 완료되었습니다."),
    PHOTO_LIKES_CREATE_SUCCESS(HttpStatus.OK, "사진 좋아요 생성이 완료되었습니다."),
    PHOTO_LIKES_DELETE_SUCCESS(HttpStatus.OK, "사진 좋아요 삭제가 완료되었습니다."),
    PHOTO_INFO_GET_SUCCESS(HttpStatus.OK, "사진 정보 조회가 성공적으로 완료되었습니다."),
    PHOTO_LIKERS_GET_SUCCESS(HttpStatus.OK, "띱한 사람 목록 조회가 성공적으로 완료되었습니다."),

    // cheese4cut
    CHEESE4CUT_GET_SUCCESS(HttpStatus.OK, "치즈네컷 조회가 성공적으로 완료되었습니다."),
    CHEESE4CUT_FINALIZE_SUCCESS(HttpStatus.OK, "치즈네컷 수동 확정이 성공적으로 완료되었습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
