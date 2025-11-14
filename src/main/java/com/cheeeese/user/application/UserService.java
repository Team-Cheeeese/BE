package com.cheeeese.user.application;

import com.cheeeese.album.domain.Album;
import com.cheeeese.album.infrastructure.persistence.UserAlbumRepository;
import com.cheeeese.photo.domain.PhotoStatus;
import com.cheeeese.photo.infrastructure.persistence.PhotoRepository;
import com.cheeeese.user.application.validator.UserValidator;
import com.cheeeese.user.domain.User;
import com.cheeeese.user.dto.request.UserAgreementRequest;
import com.cheeeese.user.dto.request.UserProfileRequest;
import com.cheeeese.user.dto.response.UserInfoResponse;
import com.cheeeese.user.exception.UserException;
import com.cheeeese.user.exception.code.UserErrorCode;
import com.cheeeese.user.infrastructure.mapper.UserMapper;
import com.cheeeese.user.infrastructure.persistence.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserValidator userValidator;
    private final UserRepository userRepository;
    private final UserAlbumRepository userAlbumRepository;
    private final PhotoRepository photoRepository;

    @Transactional
    public void updateUserProfile(User user, UserProfileRequest request) {
        // TODO: 이미지 수정 추후 추가
        user.updateUserProfile(request.name());
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

    public UserInfoResponse getUserInfo(User user) {
        long albumCount = userAlbumRepository.countByUserIdAndAlbumStatusIn(
                user.getId(),
                List.of(Album.AlbumStatus.ACTIVE, Album.AlbumStatus.EXPIRED)
        );

        long likesCount = Optional.ofNullable(
                photoRepository.sumLikesByUserIdAndStatus(user.getId(), PhotoStatus.COMPLETED)
        ).orElse(0L);

        return UserMapper.toUserInfoResponse(user, albumCount, likesCount);
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
