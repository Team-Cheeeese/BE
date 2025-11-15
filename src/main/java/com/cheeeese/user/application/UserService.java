package com.cheeeese.user.application;

import com.cheeeese.global.util.resolver.CdnUrlResolver;
import com.cheeeese.user.application.validator.UserValidator;
import com.cheeeese.user.domain.User;
import com.cheeeese.user.domain.type.ProfileImageType;
import com.cheeeese.user.dto.request.UserAgreementRequest;
import com.cheeeese.user.dto.request.UserProfileImageRequest;
import com.cheeeese.user.dto.request.UserProfileRequest;
import com.cheeeese.user.dto.response.UserProfileImageResponse;
import com.cheeeese.user.exception.UserException;
import com.cheeeese.user.exception.code.UserErrorCode;
import com.cheeeese.user.infrastructure.mapper.UserMapper;
import com.cheeeese.user.infrastructure.persistence.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserValidator userValidator;
    private final UserRepository userRepository;
    private final CdnUrlResolver cdnUrlResolver;;

    @Transactional
    public void updateUserName(User user, UserProfileRequest request) {
        user.updateUserName(request.name());
    }

    public UserProfileImageResponse getUserProfileImageOpt() {
        List<UserProfileImageResponse.ProfileImageOpt> opts =
                Arrays.stream(ProfileImageType.values())
                        .map(type -> {
                            String resolvedUrl = cdnUrlResolver.resolveProfile(type.getPath());
                            return UserMapper.toProfileImageOpt(type, resolvedUrl);
                        })
                        .toList();

        return UserMapper.toProfileImageResponse(opts);
    }

    @Transactional
    public void updateUserProfileImage(User user, UserProfileImageRequest request) {
        user.updateUserProfileImage(request.imageCode());
    }

    @Transactional
    public void saveUserAgreement(User user, UserAgreementRequest request) {
        userValidator.validateUserAgreement(request);

        user.saveUserAgreement(
                true,
                request.isServiceAgreement(),
                request.isUserInfoAgreement(),
                request.isMarketingAgreement(),
                request.isThirdPartyAgreement()
        );
    }

    @Transactional
    public void incrementPhotoCount(Long userId, int count) {
        int updated = userRepository.incrementPhotoCount(userId, count);
        if (updated != 1) {
            throw new UserException(UserErrorCode.USER_PHOTO_COUNT_INCREMENT_FAILED);
        }
    }

    @Transactional
    public void decrementPhotoCount(Long userId, int count) {
        int updated = userRepository.decrementPhotoCount(userId, count);
        if (updated != 1) {
            throw new UserException(UserErrorCode.USER_PHOTO_COUNT_DECREMENT_FAILED);
        }
    }
}
