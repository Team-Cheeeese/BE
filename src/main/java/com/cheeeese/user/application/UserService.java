package com.cheeeese.user.application;

import com.cheeeese.global.util.ProfileImageUtil;
import com.cheeeese.global.util.resolver.CdnUrlResolver;
import com.cheeeese.photo.infrastructure.persistence.PhotoLikesRepository;
import com.cheeeese.photo.infrastructure.persistence.PhotoRepository;
import com.cheeeese.user.application.validator.UserValidator;
import com.cheeeese.user.domain.User;
import com.cheeeese.user.domain.type.ProfileImageType;
import com.cheeeese.user.dto.request.UserOnboardingRequest;
import com.cheeeese.user.dto.request.UserProfileImageRequest;
import com.cheeeese.user.dto.request.UserProfileRequest;
import com.cheeeese.user.dto.response.UserInfoResponse;
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
    private final PhotoRepository photoRepository;
    private final PhotoLikesRepository photoLikesRepository;
    private final CdnUrlResolver cdnUrlResolver;

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
        ProfileImageType type = ProfileImageType.fromName(request.imageCode());
        user.updateUserProfileImage(type.name());
    }

    @Transactional
    public void saveUserOnboarding(User user, UserOnboardingRequest request) {
        userValidator.validateUserOnboarding(request);
        ProfileImageType type = ProfileImageType.fromName(request.imageCode());

        user.saveUserOnboarding(
                request.name(),
                type.name(),
                true,
                request.isServiceAgreement(),
                request.isUserInfoAgreement(),
                request.isMarketingAgreement(),
                request.isThirdPartyAgreement()
        );
    }

    public UserInfoResponse getUserInfo(User user) {
        String profileImage = ProfileImageUtil.resolveProfileImage(user, cdnUrlResolver);
        return UserMapper.toUserInfoResponse(user, profileImage);
    }

    @Transactional
    public void onAlbumUserBlacklisted(Long userId, int photoCnt, int likeCnt) {
        decrementAlbumCount(userId);
        decrementPhotoCount(userId, photoCnt);
        decrementLikeCount(userId, likeCnt);
    }

    @Transactional
    public void onPhotoDeleted(Long userId, Long albumId) {
        int likeCnt = photoLikesRepository.countLikesByAlbumAndPhotoOwner(albumId, userId);

        decrementPhotoCount(userId, 1);
        decrementLikeCount(userId, likeCnt);
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

    private void decrementAlbumCount(Long userId) {
        int updated = userRepository.decrementAlbumCnt(userId);
        if (updated != 1) {
            throw new UserException(UserErrorCode.USER_ALBUM_COUNT_DECREMENT_FAILED);
        }
    }

    @Transactional
    public void decrementLikeCount(Long userId, int count) {
        int updated = userRepository.decrementLikeCntBy(userId, count);
        if (updated != 1) {
            throw new UserException(UserErrorCode.USER_PHOTO_LIKE_COUNT_DECREMENT_FAILED);
        }
    }
}
