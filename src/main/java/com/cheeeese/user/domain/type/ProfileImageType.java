package com.cheeeese.user.domain.type;

import com.cheeeese.global.exception.BusinessException;
import com.cheeeese.user.exception.code.UserErrorCode;
import lombok.Getter;

@Getter
public enum ProfileImageType {
    P1("profile/sign_up_profile_1.jpg"),
    P2("profile/sign_up_profile_2.jpg"),
    P3("profile/sign_up_profile_3.jpg"),
    P4("profile/sign_up_profile_4.jpg"),
    P5("profile/sign_up_profile_5.jpg"),
    P6("profile/sign_up_profile_6.jpg"),
    P7("profile/sign_up_profile_7.jpg"),
    P8("profile/sign_up_profile_8.jpg"),
    P9("profile/sign_up_profile_9.jpg"),
    P10("profile/sign_up_profile_10.jpg");

    private final String path;

    ProfileImageType(String path) {
        this.path = path;
    }

    public static ProfileImageType fromName(String name) {
        if (name == null || name.isBlank()) {
            throw new BusinessException(UserErrorCode.USER_PROFILE_IMAGE_CODE_INVALID);
        }

        try {
            return ProfileImageType.valueOf(name);
        } catch (IllegalArgumentException e) {
            throw new BusinessException(UserErrorCode.USER_PROFILE_IMAGE_CODE_INVALID);
        }
    }
}
