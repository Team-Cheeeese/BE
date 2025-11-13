package com.cheeeese.album.application;

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
import com.cheeeese.album.infrastructure.persistence.AlbumRepository;
import com.cheeeese.album.infrastructure.persistence.AlbumExpirationRedisRepository;
import com.cheeeese.global.security.CustomUserDetails;
import com.cheeeese.global.util.resolver.CdnUrlResolver;
import com.cheeeese.photo.application.PhotoService;
import com.cheeeese.album.domain.UserAlbum;
import com.cheeeese.album.infrastructure.persistence.UserAlbumRepository;
import com.cheeeese.photo.domain.Photo;
import com.cheeeese.photo.domain.PhotoStatus;
import com.cheeeese.album.dto.response.AlbumBest4CutResponse;
import com.cheeeese.photo.infrastructure.mapper.PhotoMapper;
import com.cheeeese.photo.infrastructure.persistence.PhotoLikesRepository;
import com.cheeeese.photo.infrastructure.persistence.PhotoRepository;
import com.cheeeese.user.domain.User;
import com.cheeeese.user.exception.UserException;
import com.cheeeese.user.exception.code.UserErrorCode;
import com.cheeeese.user.infrastructure.persistence.UserRepository;
import com.github.f4b6a3.uuid.UuidCreator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    private final PhotoService photoService;
    private final AlbumExpirationRedisRepository albumExpirationRedisRepository;
    private final CdnUrlResolver cdnUrlResolver;

    @Transactional
    public AlbumCreationResponse createAlbum(User user, AlbumCreationRequest request) {
        String code = UuidCreator.getTimeOrdered().toString();

        // long createdThisWeek = countUserAlbumsCreatedThisWeek(user);

        albumValidator.validateAlbumCreation(request);

        Album album = AlbumMapper.toEntity(
                user.getId(),
                request.title(),
                code,
                request.themeEmoji(),
                request.participant(),
                request.eventDate(),
                true,
                LocalDateTime.now().plusDays(7)
        );
        albumRepository.save(album);

        userAlbumRepository.save(UserAlbumMapper.toEntity(
                user,
                album,
                Role.MAKER
        ));
        userRepository.incrementAlbumCnt(user.getId());

        albumExpirationRedisRepository.registerAlbum(album.getId());

        return AlbumMapper.toCreationResponse(album);
    }

    public AlbumInvitationResponse getInvitationInfo(String code) {
        Album album = albumValidator.validateAlbumCode(code);

        if (album.isExpired()) {
            return AlbumMapper.toExpiredInvitationResponse(album);
        }
        User maker = getMaker(album.getMakerId());

        return AlbumMapper.toInvitationResponse(album, maker);
    }

    @Transactional
    public AlbumEnterResponse enterAlbum(String code, User currentUser) {
        Album album = albumValidator.validateAlbumCode(code);
        albumValidator.validateAlbumEntry(album, currentUser);

        Optional<UserAlbum> existing = userAlbumRepository.findByUserIdAndAlbumId(currentUser.getId(), album.getId());
        AlbumMakerInfo makerInfo = AlbumMapper.toMakerInfo(getMaker(album.getMakerId()));

        // Case 1: 기존 참여 이력 존재
        if (existing.isPresent()) {
            UserAlbum userAlbum = existing.get();

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

        int remainingUploadSlots = calculateRemainingUploadSlots(album);

        return AlbumMapper.toNewResponse(album, makerInfo, remainingUploadSlots, recentPhotos);
    }

    public UploadAvailableCountResponse getAvailablePhotoCount(User user, String code) {
        Album album = albumValidator.validateAlbumCode(code);
        albumValidator.validateUploadPermission(album, user);

        int availableCount = calculateRemainingUploadSlots(album);

        return AlbumMapper.toAvailableCountResponse(
                availableCount,
                album.getMaxPhotoCount(),
                album.getCurrentPhotoCount()
        );
    }

    public AlbumParticipantResponse getAlbumParticipantList(Authentication authentication, String code) {

        User currentUser = extractUser(authentication);

        Album album = albumValidator.validateAlbumCode(code);

        boolean isExpired = album.isExpired();

        Role myRole = null;
        Long currentUserId = currentUser != null ? currentUser.getId() : null;

        if (currentUserId != null) {
            Optional<UserAlbum> myUserAlbumOptional = userAlbumRepository.findByUserIdAndAlbumId(currentUserId, album.getId());

            if (myUserAlbumOptional.isPresent()) {
                myRole = myUserAlbumOptional.get().getRole();
            }
        }

        // 앨범의 전체 참여자 목록
        List<UserAlbum> userAlbums = userAlbumRepository.findAllByAlbumId(album.getId());

        List<AlbumParticipantListResponse.ParticipantInfo> participantInfos = buildSortedParticipantInfos(userAlbums, currentUserId);

        return UserAlbumMapper.toAlbumParticipantResponse(
                album,
                isExpired,
                myRole,
                participantInfos
        );
    }

    public AlbumInfoResponse getAlbumInfo(User user, String code) {
        Album album = albumValidator.validateAlbumCode(code);

        albumValidator.validateAlbumParticipant(album, user);

        return PhotoMapper.toAlbumInfoResponse(album);
    }

    public List<AlbumBest4CutResponse> getAlbumBest4Cut(User user, String code) {
        Album album = albumValidator.validateAlbumCode(code);

        albumValidator.validateAlbumParticipant(album, user);

        List<Photo> topPhotos = photoRepository.findTop4CompletedPhotosByLikes(
                album.getId(),
                PhotoStatus.COMPLETED,
                PageRequest.of(0, 4)
        );

        return topPhotos.stream()
                .map(photo -> {
                    String thumbnailUrl = cdnUrlResolver.resolveThumbnail(photo.getThumbnailUrl());
                    boolean isLiked = photoLikesRepository.existsByUserIdAndPhotoId(user.getId(), photo.getId());
                    return AlbumMapper.toBest4CutResponse(photo, thumbnailUrl, isLiked);
                })
                .toList();
    }

    private User extractUser(Authentication authentication) {
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return null;
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof CustomUserDetails customUserDetails) {
            return customUserDetails.getUser();
        }
        return null;
    }

    private int calculateRemainingUploadSlots(Album album) {
        int current = album.getCurrentPhotoCount();
        int max = album.getMaxPhotoCount();
        return Math.max(0, max - current);
    }

    private long countUserAlbumsCreatedThisWeek(User user) {
        return albumRepository.countByUserAndCreatedAtBetween(
                user.getId(),
                LocalDate.now().with(DayOfWeek.MONDAY).atTime(LocalTime.MIN),
                LocalDate.now().with(DayOfWeek.MONDAY).plusWeeks(1).atTime(LocalTime.now())
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
            return List.of(AlbumMapper.toRecentPhotoResponse(photo));
        }

        // 5개인 경우, 5개 모두 반환
        return photos.stream()
                .map(AlbumMapper::toRecentPhotoResponse)
                .collect(Collectors.toList());
    }

    private List<AlbumParticipantListResponse.ParticipantInfo> buildSortedParticipantInfos(
            List<UserAlbum> userAlbums,
            Long currentUserId
    ) {
        return userAlbums.stream()
                .map(userAlbum -> {
                    User user = userAlbum.getUser();
                    Role role = userAlbum.getRole();
                    boolean isMe = currentUserId != null && user.getId().equals(currentUserId);
                    return UserAlbumMapper.toParticipantInfo(user, role, isMe);
                })
                .sorted(Comparator
                        .comparing(AlbumParticipantListResponse.ParticipantInfo::isMe, Comparator.reverseOrder())
                        .thenComparing(p -> p.role() == Role.MAKER ? 0 : 1)
                        .thenComparing(AlbumParticipantListResponse.ParticipantInfo::name)
                )
                .toList();
    }
}
