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
    USER_INFO_FETCH_SUCCESS(HttpStatus.OK, "사용자 정보 조회가 성공적으로 완료되었습니다."),
    USER_PROFILE_UPDATE_SUCCESS(HttpStatus.OK, "사용자 프로필 업데이트가 성공적으로 완료되었습니다."),
    USER_NAME_UPDATE_SUCCESS(HttpStatus.OK, "사용자 이름 업데이트가 성공적으로 완료되었습니다."),
    USER_PROFILE_IMAGE_OPT_GET_SUCCESS(HttpStatus.OK, "사용자 프로필 이미지 선택 옵션 목록 조회가 성공적으로 완료되었습니다."),
    USER_PROFILE_IMAGE_UPDATE_SUCCESS(HttpStatus.OK, "사용자 프로필 이미지 업데이트가 성공적으로 완료되었습니다."),
    USER_ONBOARDING_SUCCESS(HttpStatus.OK, "사용자 온보딩이 성공적으로 완료되었습니다."),

    // album
    ALBUM_OPEN_LIST_FETCH_SUCCESS(HttpStatus.OK, "열린 앨범 목록 조회가 성공적으로 완료되었습니다."),
    ALBUM_MY_OPEN_LIST_FETCH_SUCCESS(HttpStatus.OK, "내가 만든 열린 앨범 목록 조회가 성공적으로 완료되었습니다."),
    ALBUM_CLOSED_LIST_FETCH_SUCCESS(HttpStatus.OK, "닫힌 앨범 목록 조회가 성공적으로 완료되었습니다."),
    ALBUM_INVITATION_FETCH_SUCCESS(HttpStatus.OK, "앨범 초대장 정보 조회가 성공적으로 완료되었습니다."),
    ALBUM_ENTER_SUCCESS(HttpStatus.OK, "앨범 입장이 성공적으로 완료되었습니다."),
    ALBUM_CREATE_SUCCESS(HttpStatus.OK, "앨범 생성이 성공적으로 완료되었습니다."),
    ALBUM_PARTICIPANT_FETCH_SUCCESS(HttpStatus.OK, "앨범 참여자 조회가 성공적으로 완료되었습니다."),
    ALBUM_INFO_GET_SUCCESS(HttpStatus.OK, "앨범 정보 조회가 성공적으로 완료되었습니다."),
    ALBUM_BEST4CUT_GET_SUCCESS(HttpStatus.OK, "베스트 앨범컷 조회가 성공적으로 완료되었습니다."),
    ALBUM_LEAVE_SUCCESS(HttpStatus.OK, "앨범 나가기가 성공적으로 완료되었습니다."),
    ALBUM_USER_BLACKLISTED_SUCCESS(HttpStatus.OK, "앨범 블랙 리스트 등록이 성공적으로 완료되었습니다."),

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
    PHOTO_LIKERS_GET_SUCCESS(HttpStatus.OK, "띱한 사람 목록 조회가 성공적으로 완료되었습니다."),
    PHOTO_DELETE_SUCCESS(HttpStatus.OK, "사진 삭제가 성공적으로 완료되었습니다."),

    // cheese4cut
    CHEESE4CUT_GET_SUCCESS(HttpStatus.OK, "치즈네컷 조회가 성공적으로 완료되었습니다."),
    CHEESE4CUT_FINALIZE_SUCCESS(HttpStatus.OK, "치즈네컷 수동 확정이 성공적으로 완료되었습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
