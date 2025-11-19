package com.cheeeese.photo.application;

import com.cheeeese.album.domain.type.AlbumSorting;
import com.cheeeese.global.util.RedisCacheUtil;
import com.cheeeese.global.util.resolver.CdnUrlResolver;
import com.cheeeese.photo.application.support.PhotoReader;
import com.cheeeese.photo.domain.Photo;
import com.cheeeese.photo.domain.PhotoStatus;
import com.cheeeese.photo.dto.response.*;
import com.cheeeese.photo.exception.PhotoException;
import com.cheeeese.photo.exception.code.PhotoErrorCode;
import com.cheeeese.photo.infrastructure.mapper.PhotoMapper;
import com.cheeeese.photo.infrastructure.persistence.PhotoHistoryRepository;
import com.cheeeese.photo.infrastructure.persistence.PhotoLikesRepository;
import com.cheeeese.photo.infrastructure.persistence.PhotoRepository;
import com.cheeeese.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PhotoQueryService {

    private final PhotoRepository photoRepository;
    private final PhotoLikesRepository photoLikesRepository;
    private final PhotoHistoryRepository photoHistoryRepository;
    private final PhotoReader photoReader;
    private final RedisCacheUtil redisCacheUtil;
    private final CdnUrlResolver cdnUrlResolver;

    private static final String PHOTO_KEY = "cache:album:%s:photos:sort:%s:page:%d:size:%d:version:%d";
    private static final String VERSION_KEY = "cache:album:%s:version";

    public PhotoPageResponse getPhotoPage(User user, String code, int page, int size, AlbumSorting albumSorting) {
        String versionKey = String.format(VERSION_KEY, code);
        Long curVersion = Optional.ofNullable(redisCacheUtil.getValue(versionKey)).orElse(0L);

        String photoKey = String.format(PHOTO_KEY, code, albumSorting.getParam(), page, size, curVersion);
        PhotoPageResponse cachedList = redisCacheUtil.getObject(photoKey, PhotoPageResponse.class);

        // redis에 존재할 경우, db 접근 X + 바로 반환
        if (cachedList != null) {
            return attachUserStatus(user, cachedList);
        }
        PhotoPageResponse responses = getPhotoPageFromDB(code, page, size, albumSorting);

        redisCacheUtil.setValue(photoKey, responses, 300000L);

        return attachUserStatus(user, responses);
    }

    @Transactional
    public void invalidatePhotoCache(String code) { // TODO: 사진 삭제, 업로드 등 변화가 일어난 부분에 해당 메서드 추가
        String versionKey = String.format(VERSION_KEY, code);
        Long version = Optional.ofNullable(redisCacheUtil.getValue(versionKey)).orElse(0L);

        redisCacheUtil.setValue(versionKey, version + 1, null);

        redisCacheUtil.deletePattern("album:" + code + ":photos:*");
    }

    // TODO: 데이터 조회 로직을 Reader 클래스로 분리하는 것 고려
    public PhotoLikedPageResponse getPhotoLiked(User user, String code, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Slice<Photo> photos = photoRepository.findLikedPhotosByAlbumAndUser(code, user.getId(), PhotoStatus.COMPLETED, pageRequest);

        List<Long> photoIds = photos.getContent().stream()
                .map(Photo::getId)
                .toList();

        if (photoIds.isEmpty()) {
            return PhotoMapper.toPhotoLikedPageResponse(photos, List.of());
        }

        Set<Long> liked = findUserLikedPhotoIds(user.getId(), photoIds);
        Set<Long> downloaded = findUserDownloadedPhotoIds(user.getId(), photoIds);
        Set<Long> recent = findUserRecentlyDownloadedPhotoIds(user.getId(), photoIds);

        List<PhotoLikedResponse> responses = buildPhotoLikedResponses(photos.getContent(), liked, downloaded, recent);

        return PhotoMapper.toPhotoLikedPageResponse(photos, responses);
    }

    public PhotoDetailResponse getPhotoDetail(User user, String code, Long photoId) {
        Photo photo = photoReader.getPhotoInAlbum(photoId, code);

        String resolveOriginalUrl = cdnUrlResolver.resolveOriginal(photo.getImageUrl());
        String resolveThumbnailUrl = cdnUrlResolver.resolveThumbnail(photo.getThumbnailUrl());

        boolean isLiked = photoLikesRepository.existsByUserIdAndPhotoId(user.getId(), photo.getId());
        boolean isDownloaded = photoHistoryRepository.existsByUserIdAndPhotoId(user.getId(), photo.getId());
        boolean isRecentlyDownloaded = photoHistoryRepository.existsByUserIdAndPhotoIdAndCreatedAtAfter(
                user.getId(), photo.getId(), LocalDateTime.now().minusHours(1)
        );

        return PhotoMapper.toPhotoDetailResponse(photo, resolveOriginalUrl, resolveThumbnailUrl, isLiked, isDownloaded, isRecentlyDownloaded);
    }

    private PhotoPageResponse getPhotoPageFromDB(String code, int page, int size, AlbumSorting albumSorting) {
        PageRequest pageRequest = PageRequest.of(page, size, getPhotoSortingOption(albumSorting));
        Slice<Photo> photos = photoRepository.findAllByAlbumCodeAndStatus(code, PhotoStatus.COMPLETED, pageRequest);

        List<PhotoListResponse> responses = photos.getContent().stream()
                .map(photo -> {
                    String imageUrl = cdnUrlResolver.resolveOriginal(photo.getImageUrl());
                    String thumbnailUrl = cdnUrlResolver.resolveThumbnail(photo.getThumbnailUrl());
                    return PhotoMapper.toPhotoListResponse(photo, imageUrl, thumbnailUrl, false, false);
                })
                .toList();

        return PhotoMapper.toPhotoPageResponse(photos, responses);
    }

    private PhotoPageResponse attachUserStatus(User user, PhotoPageResponse response) {
        List<Long> photoIds = extractPhotoIds(response);
        Set<Long> likedIds = findUserLikedPhotoIds(user.getId(), photoIds);
        Set<Long> downloadedIds = findUserDownloadedPhotoIds(user.getId(), photoIds);
        Set<Long> recentlyDownloadedIds = findUserRecentlyDownloadedPhotoIds(user.getId(), photoIds);
        List<PhotoListResponse> updatedResponses = updateUserStatus(response.responses(), likedIds, downloadedIds, recentlyDownloadedIds);
        return PhotoMapper.toRebuildPhotoPageResponse(response, updatedResponses);
    }

    private List<Long> extractPhotoIds(PhotoPageResponse response) {
        return response.responses().stream()
                .map(PhotoListResponse::photoId)
                .toList();
    }

    private Set<Long> findUserLikedPhotoIds(Long userId, List<Long> photoIds) {
        return photoLikesRepository.findAllLikedPhotoIds(userId, photoIds);
    }

    private Set<Long> findUserDownloadedPhotoIds(Long userId, List<Long> photoIds) {
        return photoHistoryRepository.findDownloadedPhotoIds(userId, photoIds);
    }

    private Set<Long> findUserRecentlyDownloadedPhotoIds(Long userId, List<Long> photoIds) {
        return photoHistoryRepository.findRecentlyDownloadedPhotoIds(userId, photoIds, LocalDateTime.now().minusHours(1));
    }

    private List<PhotoListResponse> updateUserStatus(
            List<PhotoListResponse> responses,
            Set<Long> likeIds,
            Set<Long> downloadedIds,
            Set<Long> recentlyDownloadedIds
    ) {
        return responses.stream()
                .map(response -> response.withUserStatus(
                        likeIds.contains(response.photoId()),
                        downloadedIds.contains(response.photoId()),
                        recentlyDownloadedIds.contains(response.photoId())
                )).toList();
    }

    private Sort getPhotoSortingOption(AlbumSorting albumSorting) {
        return switch (albumSorting) {
            case POPULAR -> Sort.by(Sort.Order.desc("likesCnt"), Sort.Order.desc("createdAt"));
            case CAPTURED_AT -> Sort.by(Sort.Direction.DESC, "captureTime");
            case CREATED_AT -> Sort.by(Sort.Direction.DESC, "createdAt");
        };
    }

    private List<PhotoLikedResponse> buildPhotoLikedResponses(List<Photo> photos, Set<Long> liked, Set<Long> downloaded, Set<Long> recent) {
        return photos.stream()
                .map(photo -> {
                    Long id = photo.getId();
                    String imageUrl = cdnUrlResolver.resolveOriginal(photo.getImageUrl());
                    String thumbnailUrl = cdnUrlResolver.resolveThumbnail(photo.getThumbnailUrl());
                    boolean isLiked = liked.contains(id);
                    boolean isDownloaded = downloaded.contains(id);
                    boolean isRecentlyDownloaded = recent.contains(id);
                    return PhotoMapper.toPhotoLikedResponse(photo, imageUrl, thumbnailUrl, isLiked, isDownloaded, isRecentlyDownloaded);
                })
                .toList();
    }
}
