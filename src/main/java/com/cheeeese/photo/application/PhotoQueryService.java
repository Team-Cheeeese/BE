package com.cheeeese.photo.application;

import com.cheeeese.album.domain.type.AlbumSorting;
import com.cheeeese.global.util.RedisCacheUtil;
import com.cheeeese.photo.domain.Photo;
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
import java.util.HashSet;
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
    private final RedisCacheUtil redisCacheUtil;

    private static final String PHOTO_KEY = "album:%s:photos:page:%d:version:%d";
    private static final String VERSION_KEY = "album:%s:version";

    public PhotoPageResponse getPhotoPage(User user, String code, int page, int size, AlbumSorting albumSorting) {
        String versionKey = String.format(VERSION_KEY, code);
        Long curVersion = Optional.ofNullable(redisCacheUtil.getValue(versionKey)).orElse(0L);

        String photoKey = String.format(PHOTO_KEY, code, page, curVersion);
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

    public PhotoLikedPageResponse getPhotoLiked(User user, String code, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Slice<Photo> photos = photoRepository.findLikedPhotosByAlbumAndUser(code, user.getId(), pageRequest);

        List<PhotoLikedResponse> responses = photos.getContent().stream()
                .map(photo -> {
                    boolean isDownloaded = photoHistoryRepository.existsByUserIdAndPhotoId(user.getId(), photo.getId());
                    return PhotoMapper.toPhotoLikedResponse(photo, isDownloaded);
                })
                .toList();

        return PhotoMapper.toPhotoLikedPageResponse(photos, responses);
    }

    public PhotoDetailResponse getPhotoDetail(User user, String code, Long photoId) {
        Photo photo = photoRepository.findByIdAndAlbum_Code(photoId, code)
                .orElseThrow(() -> new PhotoException(PhotoErrorCode.PHOTO_ID_NOT_FOUND));

        boolean isLiked = photoLikesRepository.existsByUserIdAndPhotoId(user.getId(), photo.getId());
        boolean isDownloaded = photoHistoryRepository.existsByUserIdAndPhotoId(user.getId(), photo.getId());

        return PhotoMapper.toPhotoDetailResponse(photo, isLiked, isDownloaded);
    }

    private PhotoPageResponse getPhotoPageFromDB(String code, int page, int size, AlbumSorting albumSorting) {
        PageRequest pageRequest = PageRequest.of(page, size, getPhotoSortingOption(albumSorting));
        Slice<Photo> photos = photoRepository.findAllByAlbumCode(code, pageRequest);
        return PhotoMapper.toPhotoPageResponse(photos);
    }

    private PhotoPageResponse attachUserStatus(User user, PhotoPageResponse response) {
        List<Long> photoIds = extractPhotoIds(response);
        Set<Long> likedIds = findUserLikedPhotoIds(user.getId(), photoIds);
        Set<Long> downloadedIds = findUserDownloadedPhotoIds(user.getId(), photoIds);
        List<PhotoListResponse> updatedResponses = updateUserStatus(response.responses(), likedIds, downloadedIds);
        return PhotoMapper.toRebuildPhotoPageResponse(response, updatedResponses);
    }

    private List<Long> extractPhotoIds(PhotoPageResponse response) {
        return response.responses().stream()
                .map(PhotoListResponse::photoId)
                .toList();
    }

    private Set<Long> findUserLikedPhotoIds(Long userId, List<Long> photoIds) {
        return new HashSet<>(photoLikesRepository.findAllLikedPhotoIds(userId, photoIds));
    }

    private Set<Long> findUserDownloadedPhotoIds(Long userId, List<Long> photoIds) {
        return new HashSet<>(photoHistoryRepository.findAllHistoryPhotoIds(userId, photoIds, LocalDateTime.now().minusHours(1)));
    }

    private List<PhotoListResponse> updateUserStatus(
            List<PhotoListResponse> responses,
            Set<Long> likeIds,
            Set<Long> downloadedIds
    ) {
        return responses.stream()
                .map(response -> response.withUserStatus(
                        likeIds.contains(response.photoId()),
                        downloadedIds.contains(response.photoId())
                )).toList();
    }

    private Sort getPhotoSortingOption(AlbumSorting albumSorting) {
        return switch (albumSorting) {
            case POPULAR -> Sort.by(Sort.Order.desc("likesCnt"), Sort.Order.desc("createdAt"));
            case CAPTURED_AT -> Sort.by(Sort.Direction.DESC, "captureTime");
            case CREATED_AT -> Sort.by(Sort.Direction.DESC, "createdAt");
        };
    }
}
