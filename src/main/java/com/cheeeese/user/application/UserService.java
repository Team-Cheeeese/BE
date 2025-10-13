package com.cheeeese.user.application;

import com.cheeeese.user.application.validator.UserValidator;
import com.cheeeese.user.domain.User;
import com.cheeeese.user.dto.request.UserAgreementRequest;
import com.cheeeese.user.dto.request.UserProfileRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserValidator userValidator;

    @Transactional
    public void updateUserProfile(User user, UserProfileRequest request) {
        user.updateUserProfile(request.name());
    }

    @Transactional
    public void saveUserAgreement(User user, UserAgreementRequest request) {
        userValidator.validateUserAgreement(request);

        user.saveUserAgreement(
                true,
                true,
                request.isMarketingAgreement(),
                request.isThirdPartyAgreement()
        );
    }
}
