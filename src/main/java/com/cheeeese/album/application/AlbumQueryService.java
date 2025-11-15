package com.cheeeese.album.application;

import com.cheeeese.album.domain.Album;
import com.cheeeese.album.domain.type.Role;
import com.cheeeese.album.dto.response.ClosedAlbumPageResponse;
import com.cheeeese.album.dto.response.ClosedAlbumSummaryResponse;
import com.cheeeese.album.dto.response.OpenAlbumPageResponse;
import com.cheeeese.album.dto.response.OpenAlbumSummaryResponse;
import com.cheeeese.album.infrastructure.mapper.AlbumQueryMapper;
import com.cheeeese.album.infrastructure.persistence.UserAlbumRepository;
import com.cheeeese.cheese4cut.domain.Cheese4cutPhoto;
import com.cheeeese.cheese4cut.infrastructure.persistence.Cheese4cutPhotoRepository;
import com.cheeeese.global.util.resolver.CdnUrlResolver;
import com.cheeeese.photo.domain.Photo;
import com.cheeeese.photo.domain.PhotoStatus;
import com.cheeeese.photo.infrastructure.persistence.PhotoRepository;
import com.cheeeese.user.domain.User;
import com.cheeeese.user.exception.UserException;
import com.cheeeese.user.exception.code.UserErrorCode;
import com.cheeeese.user.infrastructure.persistence.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
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
    private final Cheese4cutPhotoRepository cheese4cutPhotoRepository;
    private final CdnUrlResolver cdnUrlResolver;

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

        List<Photo> photos = photoRepository.findRecentPhotosByAlbumIdsAndStatus(
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
}
