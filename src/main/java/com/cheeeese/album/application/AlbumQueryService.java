package com.cheeeese.album.application;

import com.cheeeese.album.application.validator.AlbumValidator;
import com.cheeeese.album.domain.Album;
import com.cheeeese.album.domain.UserAlbum;
import com.cheeeese.album.domain.type.Role;
import com.cheeeese.album.dto.response.*;
import com.cheeeese.album.exception.AlbumException;
import com.cheeeese.album.exception.code.AlbumErrorCode;
import com.cheeeese.album.infrastructure.mapper.AlbumMapper;
import com.cheeeese.album.infrastructure.mapper.AlbumQueryMapper;
import com.cheeeese.album.infrastructure.mapper.UserAlbumMapper;
import com.cheeeese.album.infrastructure.persistence.UserAlbumRepository;
import com.cheeeese.cheese4cut.domain.Cheese4cutPhoto;
import com.cheeeese.cheese4cut.infrastructure.persistence.Cheese4cutPhotoRepository;
import com.cheeeese.global.security.CustomUserDetails;
import com.cheeeese.global.util.resolver.CdnUrlResolver;
import com.cheeeese.photo.domain.Photo;
import com.cheeeese.photo.domain.PhotoStatus;
import com.cheeeese.photo.infrastructure.persistence.PhotoLikesRepository;
import com.cheeeese.photo.infrastructure.persistence.PhotoRepository;
import com.cheeeese.user.domain.User;
import com.cheeeese.user.domain.type.ProfileImageType;
import com.cheeeese.user.exception.UserException;
import com.cheeeese.user.exception.code.UserErrorCode;
import com.cheeeese.user.infrastructure.persistence.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AlbumQueryService {

    private static final int RECENT_THUMBNAIL_COUNT = 3;

    private final UserAlbumRepository userAlbumRepository;
    private final UserRepository userRepository;
    private final PhotoRepository photoRepository;
    private final PhotoLikesRepository photoLikesRepository;
    private final Cheese4cutPhotoRepository cheese4cutPhotoRepository;
    private final CdnUrlResolver cdnUrlResolver;
    private final AlbumValidator albumValidator;

    public UploadAvailableCountResponse getAvailablePhotoCount(String code) {
        Album album = albumValidator.validateAlbumCode(code);

        int availableCount = album.getRemainingUploadSlots();

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
        List<UserAlbum> userAlbums = userAlbumRepository.findAllByAlbumIdExcludeBlack(album.getId(), Role.BLACK);

        List<AlbumParticipantListResponse.ParticipantInfo> participantInfos = buildSortedParticipantInfos(userAlbums, currentUserId);

        return UserAlbumMapper.toAlbumParticipantResponse(
                album,
                isExpired,
                myRole,
                participantInfos
        );
    }

    public AlbumInfoResponse getAlbumInfo(String code) {
        Album album = albumValidator.validateAlbumCode(code);

        User maker = userAlbumRepository.findMakerByAlbumId(album.getId(), Role.MAKER)
                .map(UserAlbum::getUser)
                .orElseThrow(() -> new AlbumException(AlbumErrorCode.USER_NOT_MAKER));

        return AlbumMapper.toAlbumInfoResponse(album, maker);
    }

    public List<AlbumBest4CutResponse> getAlbumBest4Cut(User user, String code) {
        Album album = albumValidator.validateAlbumCode(code);

        albumValidator.validateAlbumParticipant(album, user);

        List<Photo> topPhotos = photoRepository.findTop4CompletedPhotosByLikes(
                album.getId(),
                PhotoStatus.COMPLETED,
                PageRequest.of(0, 4)
        );

        List<Long> photoIds = topPhotos.stream()
                .map(Photo::getId)
                .toList();

        Set<Long> likedPhotoIds = photoLikesRepository.findAllLikedPhotoIds(user.getId(), photoIds);

        return topPhotos.stream()
                .map(photo -> {
                    String thumbnailUrl = cdnUrlResolver.resolveThumbnail(photo.getThumbnailUrl());
                    boolean isLiked = likedPhotoIds.contains(photo.getId());
                    return AlbumMapper.toBest4CutResponse(photo, thumbnailUrl, isLiked);
                })
                .toList();
    }

    public OpenAlbumPageResponse getOpenAlbums(User user, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Slice<Album> albums = userAlbumRepository.findOpenAlbumsByUserId(
                user.getId(),
                Album.AlbumStatus.ACTIVE,
                LocalDateTime.now(),
                pageable
        );

        List<OpenAlbumSummaryResponse> responses = buildOpenAlbumResponses(albums.getContent());

        return AlbumQueryMapper.toOpenAlbumPageResponse(responses, albums);
    }

    public OpenAlbumPageResponse getMyOpenAlbums(User user, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Slice<Album> albums = userAlbumRepository.findOpenAlbumsByUserIdAndRole(
                user.getId(),
                Role.MAKER,
                Album.AlbumStatus.ACTIVE,
                LocalDateTime.now(),
                pageable
        );

        List<OpenAlbumSummaryResponse> responses = buildOpenAlbumResponses(albums.getContent());

        return AlbumQueryMapper.toOpenAlbumPageResponse(responses, albums);
    }

    public ClosedAlbumPageResponse getClosedAlbums(User user, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Slice<Album> expiredAlbums = userAlbumRepository.findClosedAlbumsByUserId(
                user.getId(),
                Album.AlbumStatus.EXPIRED,
                pageable
        );

        List<Long> albumIds = expiredAlbums.getContent().stream()
                .map(Album::getId)
                .toList();

        if (albumIds.isEmpty()) {
            return AlbumQueryMapper.toClosedAlbumPageResponse(List.of(), expiredAlbums);
        }

        Map<Long, User> makerMap = getMakers(expiredAlbums.getContent());

        List<Cheese4cutPhoto> allCheese4cutPhotos = cheese4cutPhotoRepository.findAllCheese4cutPhotosByAlbumIds(albumIds);

        Map<Long, List<Cheese4cutPhoto>> cheese4cutPhotoMap = allCheese4cutPhotos.stream()
                .collect(Collectors.groupingBy(
                        c4p -> c4p.getCheese4cut().getAlbum().getId(),
                        Collectors.toList()
                ));

        List<ClosedAlbumSummaryResponse> responses = expiredAlbums.getContent().stream()
                .map(album -> {
                    List<Cheese4cutPhoto> c4pList = cheese4cutPhotoMap.getOrDefault(album.getId(), List.of());

                    User maker = Optional.ofNullable(makerMap.get(album.getMakerId()))
                            .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

                    List<String> thumbnails = c4pList.stream()
                            .sorted(Comparator.comparingInt(Cheese4cutPhoto::getPhotoRank)) // photoRank 순으로 정렬
                            .map(Cheese4cutPhoto::getThumbnailImageUrl)
                            .map(cdnUrlResolver::resolveThumbnail)
                            .collect(Collectors.toList());

                    return AlbumQueryMapper.toClosedAlbumSummaryResponse(album, maker, thumbnails);
                })
                .collect(Collectors.toList());

        return AlbumQueryMapper.toClosedAlbumPageResponse(responses, expiredAlbums);
    }

    private List<OpenAlbumSummaryResponse> buildOpenAlbumResponses(List<Album> albums) {
        if (albums.isEmpty()) {
            return List.of();
        }

        Map<Long, User> makerMap = getMakers(albums);
        Map<Long, List<String>> recentThumbnailsMap = getRecentThumbnailsMap(albums);

        return albums.stream()
                .map(album -> {
                    User maker = Optional.ofNullable(makerMap.get(album.getMakerId()))
                            .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

                    List<String> recentThumbnails = recentThumbnailsMap.getOrDefault(album.getId(), List.of());
                    return AlbumQueryMapper.toOpenAlbumSummaryResponse(album, maker, recentThumbnails);
                })
                .toList();
    }

    private Map<Long, User> getMakers(List<Album> albums) {
        List<Long> makerIds = albums.stream()
                .map(Album::getMakerId)
                .distinct()
                .toList();
        Map<Long, User> makers = userRepository.findAllById(makerIds).stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));
        if (makers.size() != makerIds.size()) {
            throw new UserException(UserErrorCode.USER_NOT_FOUND);
        }
        return makers;
    }

    private Map<Long, List<String>> getRecentThumbnailsMap(List<Album> albums) {
        List<Long> albumIds = albums.stream()
                .map(Album::getId)
                .toList();

        if (albumIds.isEmpty()) {
            return Map.of();
        }

        List<Photo> photos = photoRepository.findTop3RecentPhotosInEachAlbum(
                albumIds,
                PhotoStatus.COMPLETED
        );

        Map<Long, List<String>> thumbnailsMap = new HashMap<>();

        for (Photo photo : photos) {
            Long albumId = photo.getAlbum().getId();
            List<String> thumbnails = thumbnailsMap.computeIfAbsent(albumId, key -> new ArrayList<>());

            if (thumbnails.size() < RECENT_THUMBNAIL_COUNT) {
                thumbnails.add(cdnUrlResolver.resolveThumbnail(photo.getThumbnailUrl()));
            }
        }

        return thumbnailsMap.entrySet().stream()
                .filter(entry -> entry.getValue().size() == RECENT_THUMBNAIL_COUNT)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> List.copyOf(entry.getValue())
                ));
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

    private List<AlbumParticipantListResponse.ParticipantInfo> buildSortedParticipantInfos(
            List<UserAlbum> userAlbums,
            Long currentUserId
    ) {
        return userAlbums.stream()
                .map(userAlbum -> {
                    User user = userAlbum.getUser();
                    Role role = userAlbum.getRole();
                    ProfileImageType type = ProfileImageType.fromName(user.getProfileImage());
                    String profileImageUrl = cdnUrlResolver.resolveProfile(type.getPath());
                    boolean isMe = currentUserId != null && user.getId().equals(currentUserId);

                    return UserAlbumMapper.toParticipantInfo(user, profileImageUrl, role, isMe);
                })
                .sorted(Comparator
                        .comparing(AlbumParticipantListResponse.ParticipantInfo::isMe, Comparator.reverseOrder())
                        .thenComparing(p -> p.role() == Role.MAKER ? 0 : 1)
                        .thenComparing(AlbumParticipantListResponse.ParticipantInfo::name)
                )
                .toList();
    }
}
