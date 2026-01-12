package com.cheeeese.album.application;

import com.cheeeese.album.application.logger.AlbumLogger;
import com.cheeeese.album.application.support.AlbumReader;
import com.cheeeese.album.application.validator.AlbumValidator;
import com.cheeeese.album.domain.Album;
import com.cheeeese.album.domain.type.AlbumJoinStatus;
import com.cheeeese.album.domain.type.Role;
import com.cheeeese.album.dto.request.AlbumCreationRequest;
import com.cheeeese.album.dto.response.*;
import com.cheeeese.album.exception.AlbumException;
import com.cheeeese.album.exception.code.AlbumErrorCode;
import com.cheeeese.album.infrastructure.mapper.AlbumMapper;
import com.cheeeese.album.infrastructure.mapper.UserAlbumMapper;
import com.cheeeese.album.infrastructure.persistence.AlbumExpirationRedisRepository;
import com.cheeeese.album.infrastructure.persistence.AlbumRepository;
import com.cheeeese.global.util.ProfileImageUtil;
import com.cheeeese.global.util.resolver.CdnUrlResolver;
import com.cheeeese.photo.application.PhotoQueryService;
import com.cheeeese.photo.application.PhotoService;
import com.cheeeese.album.domain.UserAlbum;
import com.cheeeese.album.infrastructure.persistence.UserAlbumRepository;
import com.cheeeese.photo.domain.Photo;
import com.cheeeese.photo.infrastructure.persistence.PhotoLikesRepository;
import com.cheeeese.photo.infrastructure.persistence.PhotoRepository;
import com.cheeeese.user.application.UserService;
import com.cheeeese.user.domain.User;
import com.cheeeese.user.exception.UserException;
import com.cheeeese.user.exception.code.UserErrorCode;
import com.cheeeese.user.infrastructure.persistence.UserRepository;
import com.github.f4b6a3.uuid.UuidCreator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AlbumService {

    private final AlbumValidator albumValidator;
    private final AlbumRepository albumRepository;
    private final UserAlbumRepository userAlbumRepository;
    private final UserRepository userRepository;
    private final PhotoRepository photoRepository;
    private final PhotoLikesRepository photoLikesRepository;
    private final UserService userService;
    private final PhotoService photoService;
    private final PhotoQueryService photoQueryService;
    private final AlbumExpirationRedisRepository albumExpirationRedisRepository;
    private final CdnUrlResolver cdnUrlResolver;
    private final AlbumReader albumReader;
    private final AlbumLogger albumLogger;

    @Transactional
    public AlbumCreationResponse createAlbum(User user, AlbumCreationRequest request) {
        String code = UuidCreator.getTimeOrdered().toString();

        long createdThisWeek = countUserAlbumsCreatedThisWeek(user);

        albumValidator.validateAlbumCreation(createdThisWeek, request);

        LocalDateTime expiredAt = LocalDateTime.now().plusDays(7);

        Album album = AlbumMapper.toEntity(
                user.getId(),
                request.title(),
                code,
                request.themeEmoji(),
                request.participant(),
                request.eventDate(),
                true,
                expiredAt
        );
        boolean isFirst = !albumRepository.existsByMakerId(user.getId());

        albumRepository.save(album);

        userAlbumRepository.save(UserAlbumMapper.toEntity(
                user,
                album,
                Role.MAKER
        ));
        userRepository.incrementAlbumCnt(user.getId());

        albumExpirationRedisRepository.registerAlbum(album.getId(), expiredAt);

        albumLogger.logAlbumCreated(user.getId(), album.getCode(), request.participant());

        return AlbumMapper.toCreationResponse(album, isFirst);
    }

    public AlbumInvitationResponse getInvitationInfo(String code) {
        Album album = albumValidator.validateAlbumCode(code);

        if (album.isExpired()) {
            return AlbumMapper.toExpiredInvitationResponse(album);
        }
        User maker = getMaker(album.getMakerId());
        String makerProfileUrl = ProfileImageUtil.resolveProfileImage(maker, cdnUrlResolver);

        return AlbumMapper.toInvitationResponse(album, maker, makerProfileUrl);
    }

    @Transactional
    public AlbumEnterResponse enterAlbum(String code, User currentUser) {
        Album album = albumValidator.validateAlbumCode(code);
        albumValidator.validateAlbumEntry(album, currentUser);

        Optional<UserAlbum> existing = userAlbumRepository.findByUserIdAndAlbumId(currentUser.getId(), album.getId());

        User maker = getMaker(album.getMakerId());
        String makerProfileUrl = ProfileImageUtil.resolveProfileImage(maker, cdnUrlResolver);
        AlbumMakerInfo makerInfo = AlbumMapper.toMakerInfo(maker, makerProfileUrl);

        // Case 1: 기존 참여 이력 존재
        if (existing.isPresent()) {
            UserAlbum userAlbum = existing.get();

            // 재방문 로그
            albumLogger.logAlbumViewed(currentUser.getId(), album.getCode(), userAlbum.getRole());

            if (!userAlbum.isVisible()) {
                userAlbum.show();
                return AlbumMapper.toExistingResponse(album, AlbumJoinStatus.REJOINED, makerInfo);
            }

            return AlbumMapper.toExistingResponse(album, AlbumJoinStatus.EXISTING, makerInfo);
        }

        // Case 2: 신규 참여
        albumValidator.validateAlbumCapacity(album);
        userAlbumRepository.save(UserAlbumMapper.toEntity(
                currentUser,
                album,
                Role.GUEST
        ));

        int updated = albumRepository.incrementParticipantCountAtomically(album.getId());
        if (updated == 0) {
            throw new AlbumException(AlbumErrorCode.ALBUM_MAX_PARTICIPANT_REACHED);
        }
        userRepository.incrementAlbumCnt(currentUser.getId());

        List<NewEnterResponse.RecentPhotoResponse> recentPhotos = getRecentPhotosWithUploaderInfo(album.getId());

        int remainingUploadSlots = album.getRemainingUploadSlots();

        boolean photoExist = album.getCurrentPhotoCount() > 0;
        albumLogger.logAlbumJoined(currentUser.getId(), album.getCode(), photoExist);

        return AlbumMapper.toNewResponse(album, makerInfo, remainingUploadSlots, recentPhotos);
    }

    @Transactional
    public void blacklistUser(User user, String code, Long targetUserId) {
        Album album = albumValidator.validateAlbumCode(code);

        UserAlbum requesterAlbum = albumReader.getAlbumParticipant(user.getId(), album.getId());

        albumValidator.validateBlacklistPermission(requesterAlbum);
        albumValidator.validateBlacklistTarget(user, targetUserId);

        UserAlbum targetAlbum = albumReader.getAlbumParticipant(targetUserId, album.getId());

        targetAlbum.hide();
        targetAlbum.blacklist();

        // 사진 삭제 전, 사진 수와 띱 수 먼저 계산
        int photoCnt = photoRepository.countByAlbumIdAndUserId(album.getId(), targetUserId);
        int likeCnt = photoLikesRepository.countLikesByAlbumAndPhotoOwner(album.getId(), targetUserId);

        List<Long> photoIds = photoRepository.findIdsByAlbumIdAndUserId(
                album.getId(), targetUserId
        );

        if (!photoIds.isEmpty()) {
            photoLikesRepository.deleteAllByPhotoIds(photoIds);
            photoRepository.deleteAllByIds(photoIds);
        }
        userService.onAlbumUserBlacklisted(targetUserId, photoCnt, likeCnt);

        albumRepository.decrementParticipantCount(album.getId());

        photoQueryService.invalidatePhotoCache(album.getCode());
    }

    @Transactional
    public void leaveAlbum(User user, String code) {
        Album album = albumValidator.validateAlbumCode(code);
        albumValidator.validateMakerLeaveAllowed(album, user);

        UserAlbum userAlbum = albumReader.getAlbumParticipant(user.getId(), album.getId());

        userAlbum.hide();
    }

    private long countUserAlbumsCreatedThisWeek(User user) {
        return albumRepository.countByUserAndCreatedAtBetween(
                user.getId(),
                LocalDateTime.now().minusDays(7),
                LocalDateTime.now()
        );
    }

    private User getMaker(Long makerId) {
        return userRepository.findById(makerId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
    }

    private List<NewEnterResponse.RecentPhotoResponse> getRecentPhotosWithUploaderInfo(Long albumId) {
        List<Photo> photos = photoService.getRecentPhotosForNewEnter(albumId);

        if (photos.isEmpty()) {
            return List.of();
        }

        // 1~4개인 경우, 1개만 반환하는 비즈니스 로직 적용
        if (photos.size() < 5) {
            Photo photo = photos.get(0);
            String profileUrl = ProfileImageUtil.resolveProfileImage(photo.getUser(), cdnUrlResolver);
            return List.of(AlbumMapper.toRecentPhotoResponse(photo, profileUrl));
        }

        // 5개인 경우, 5개 모두 반환
        return photos.stream()
                .map(photo -> {
                    String profileUrl = ProfileImageUtil.resolveProfileImage(
                            photo.getUser(),
                            cdnUrlResolver
                    );
                    return AlbumMapper.toRecentPhotoResponse(photo, profileUrl);
                })
                .toList();
    }
}
