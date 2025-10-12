package com.cheeeese.user.application;

import com.cheeeese.user.domain.User;
import com.cheeeese.user.dto.request.UserOnboardingRequest;
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
    public void saveUserOnboarding(User user, UserOnboardingRequest request) {
        user.saveUserOnboarding(request.name());
    }
}
