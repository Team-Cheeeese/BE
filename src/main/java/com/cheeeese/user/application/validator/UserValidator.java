package com.cheeeese.user.application.validator;

import com.cheeeese.user.dto.request.UserOnboardingRequest;
import com.cheeeese.user.exception.UserException;
import com.cheeeese.user.exception.code.UserErrorCode;
import org.springframework.stereotype.Component;

@Component
public class UserValidator {

    public void validateUserOnboarding(UserOnboardingRequest request) {
        if (request.name().isBlank()) {
            throw new UserException(UserErrorCode.USER_NAME_REQUIRED);
        }

        if (request.imageCode().isBlank()) {
            throw new UserException(UserErrorCode.USER_PROFILE_IMAGE_CODE_REQUIRED);
        }

        if (!request.isServiceAgreement() || !request.isUserInfoAgreement() || !request.isThirdPartyAgreement()) {
            throw new UserException(UserErrorCode.REQUIRED_TERMS_NOT_AGREED);
        }
    }
}
