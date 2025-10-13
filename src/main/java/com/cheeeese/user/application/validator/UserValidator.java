package com.cheeeese.user.application.validator;

import com.cheeeese.user.dto.request.UserAgreementRequest;
import com.cheeeese.user.exception.UserException;
import com.cheeeese.user.exception.code.UserErrorCode;
import org.springframework.stereotype.Component;

@Component
public class UserValidator {

    public void validateUserAgreement(UserAgreementRequest request) {
        if (!request.isServiceAgreement() || !request.isUserInfoAgreement()) {
            throw new UserException(UserErrorCode.REQUIRED_TERMS_NOT_AGREED);
        }
    }
}
