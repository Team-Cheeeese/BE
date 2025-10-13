package com.cheeeese.user.application;

import com.cheeeese.user.domain.User;
import com.cheeeese.user.dto.request.UserAgreementRequest;
import com.cheeeese.user.dto.request.UserProfileRequest;
import com.cheeeese.user.infrastructure.persistence.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public void updateUserProfile(User user, UserProfileRequest request) {
        user.updateUserProfile(request.name());
    }

    @Transactional
    public void saveUserAgreement(User user, UserAgreementRequest request) {
        user.saveUserAgreement(
                true,
                true,
                request.isMarketingAgreement(),
                request.isThirdPartyAgreement()
        );
    }
}
