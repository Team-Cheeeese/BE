package com.cheeeese.photo.application;

import com.cheeeese.album.application.validator.AlbumValidator;
import com.cheeeese.album.domain.Album;
import com.cheeeese.global.util.S3Util;
import com.cheeeese.photo.application.validator.PhotoValidator;
import com.cheeeese.photo.domain.Photo;
import com.cheeeese.photo.domain.PhotoHistory;
import com.cheeeese.photo.domain.PhotoLikes;
import com.cheeeese.photo.domain.PhotoStatus;
import com.cheeeese.photo.dto.request.PhotoDownloadRequest;
import com.cheeeese.photo.dto.request.PhotoPresignedUrlRequest;
import com.cheeeese.photo.dto.request.PhotoUploadReportRequest;
import com.cheeeese.photo.dto.response.PhotoDownloadResponse;
import com.cheeeese.photo.dto.response.PhotoPresignedUrlResponse;
import com.cheeeese.photo.exception.PhotoException;
import com.cheeeese.photo.exception.code.PhotoErrorCode;
import com.cheeeese.photo.infrastructure.mapper.PhotoHistoryMapper;
import com.cheeeese.photo.infrastructure.mapper.PhotoLikesMapper;
import com.cheeeese.photo.infrastructure.mapper.PhotoMapper;
import com.cheeeese.photo.infrastructure.persistence.PhotoHistoryRepository;
import com.cheeeese.photo.infrastructure.persistence.PhotoLikesRepository;
import com.cheeeese.photo.infrastructure.persistence.PhotoRepository;
import com.cheeeese.user.domain.User;
import com.cheeeese.user.infrastructure.persistence.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PhotoService {

    private final UserRepository userRepository;
    private final PhotoRepository photoRepository;
    private final PhotoLikesRepository photoLikesRepository;
    private final PhotoHistoryRepository photoHistoryRepository;
    private final PhotoValidator photoValidator;
    private final AlbumValidator albumValidator;
    private final PresignedUrlService presignedUrlService;
    private final PhotoQueryService photoQueryService;

    @Value("${ncp.object-storage.bucket}")
    private String bucket;

    private static final String ORIGINAL_PHOTO_PATH_FORMAT = "album/%s/original/%d_%s";

    public List<Photo> getRecentPhotosForNewEnter(Long albumId) {
        return photoRepository.findRecentPhotosByAlbumIdAndStatus(
                albumId,
                PhotoStatus.COMPLETED,
                PageRequest.of(0, 5)
        );
    }

    @Transactional
    public PhotoPresignedUrlResponse createPresignedUrls(User user, PhotoPresignedUrlRequest request) {
        Album album = validateAlbumAndPermission(user, request.albumCode());

        long currentActiveCount = photoRepository.countActivePhotosByAlbumId(album.getId());

        validateUploadRequest(album, request, currentActiveCount);

        List<PhotoPresignedUrlResponse.PresignedUrlInfo> presignedUrls = generatePresignedUrls(user, album, request.fileInfos());

        return PhotoMapper.toPresignedUrlResponse(presignedUrls);
    }

    @Transactional
    public PhotoDownloadResponse getDownloadPresignedUrls(User user, PhotoDownloadRequest request) {
        Album album = validateAlbumAndPermission(user, request.code());

        List<Photo> photos = photoRepository.findAllByIdIn(request.photoIds());

        albumValidator.validateDownloadPermission(album, user, photos);

        Set<Long> recentDownloadIds = photoHistoryRepository.findRecentlyDownloadedPhotoIds(
                user.getId(),
                request.photoIds(),
                LocalDateTime.now().minusHours(1)
        );

        List<PhotoDownloadResponse.DownloadFileInfo> presignedUrls = generateDownloadPresignedUrls(
                photos, recentDownloadIds
        );

        photos.stream()
                .filter(photo -> !recentDownloadIds.contains(photo.getId()))
                .forEach(photo -> photoHistoryRepository.findByUserIdAndPhotoId(user.getId(), photo.getId())
                        .ifPresentOrElse(
                                PhotoHistory::touch,
                                () -> photoHistoryRepository.save(PhotoHistoryMapper.toEntity(user, photo))
                        )
                );

        return PhotoMapper.toPhotoDownloadResponse(presignedUrls);
    }

    @Transactional
    public void reportUploadResult(User user, PhotoUploadReportRequest request) {
        List<Long> failurePhotoIds = request.failurePhotoIds().stream()
                .distinct()
                .toList();

        PhotoValidator.ValidatedPhotos validated = photoValidator.validatePhotos(user.getId(), failurePhotoIds);
        Long albumId = validated.albumId();

        handleFailedUploads(user, albumId, failurePhotoIds);
    }

    @Transactional
    public void createPhotoLikes(User user, Long photoId) {
        Photo photo = photoRepository.findById(photoId)
                .orElseThrow(() -> new PhotoException(PhotoErrorCode.PHOTO_NOT_FOUND));

        PhotoLikes photoLikes = PhotoLikesMapper.toEntity(user, photo);

        photoRepository.incrementLikeCnt(photo.getId());
        photoLikesRepository.save(photoLikes);

        userRepository.incrementLikeCnt(photo.getUser().getId());

        photoQueryService.invalidatePhotoCache(photo.getAlbum().getCode());
    }

    @Transactional
    public void deletePhotoLikes(User user, Long photoId) {
        Photo photo = photoRepository.findById(photoId)
                .orElseThrow(() -> new PhotoException(PhotoErrorCode.PHOTO_NOT_FOUND));

        PhotoLikes photoLikes = photoLikesRepository.findByUserIdAndPhotoId(user.getId(), photo.getId())
                .orElseThrow(() -> new PhotoException(PhotoErrorCode.PHOTO_LIKES_NOT_FOUND));

        photoRepository.decrementLikeCnt(photo.getId());
        photoLikesRepository.delete(photoLikes);

        userRepository.decrementLikeCnt(photo.getUser().getId());

        photoQueryService.invalidatePhotoCache(photo.getAlbum().getCode());
    }

    private Album validateAlbumAndPermission(User user, String albumCode) {
        Album album = albumValidator.validateAlbumCode(albumCode);
        albumValidator.validateUploadPermission(album, user);
        return album;
    }

    private void validateUploadRequest(Album album, PhotoPresignedUrlRequest request, long currentActiveCount) {
        int maxCount = album.getMaxPhotoCount();
        int requestedCount = request.fileInfos().size();

        photoValidator.validatePhotoCount(currentActiveCount, requestedCount, maxCount);
        photoValidator.validateFileInfos(request.fileInfos());
    }

    private List<PhotoPresignedUrlResponse.PresignedUrlInfo> generatePresignedUrls(
            User user,
            Album album,
            List<PhotoPresignedUrlRequest.FileInfo> fileInfos
    ) {
        return fileInfos.stream()
                .map(file -> createPresignedUrlForFile(user, album, file))
                .collect(Collectors.toList());
    }

    private List<PhotoDownloadResponse.DownloadFileInfo> generateDownloadPresignedUrls(
            List<Photo> photos,
            Set<Long> recentDownloadedIds
    ) {
        return photos.stream()
                .map(photo -> createPresignedUrlForDownload(photo, recentDownloadedIds))
                .toList();
    }

    private PhotoPresignedUrlResponse.PresignedUrlInfo createPresignedUrlForFile(
            User user,
            Album album,
            PhotoPresignedUrlRequest.FileInfo file
    ) {
        Photo photo = PhotoMapper.toEntity(user, album);
        photoRepository.save(photo);

        String safeFileName = sanitizeFileName(file.fileName());
        String objectKey = String.format(
                ORIGINAL_PHOTO_PATH_FORMAT,
                album.getCode(),
                photo.getId(),
                safeFileName
        );
        String imageUrl = bucket + "/" + objectKey;
        photo.updateImageUrl(imageUrl);

        String uploadUrl = presignedUrlService.generatePresignedPutUrl(objectKey, file.contentType());

        return PhotoMapper.toPresignedUrlInfo(photo.getId(), uploadUrl);
    }

    private PhotoDownloadResponse.DownloadFileInfo createPresignedUrlForDownload(Photo photo, Set<Long> recentDownloadedIds) {
        String fileName = S3Util.extractFileName(photo.getImageUrl());

        // 1시간 이내 다운로드 O -> null 반환
        if (recentDownloadedIds.contains(photo.getId())) {
            return PhotoMapper.toDownloadPresignedUrlInfo(photo, fileName, null);
        }

        String objectKey = S3Util.extractObjectKey(photo.getImageUrl());
        String url = presignedUrlService.generatePresignedGetUrl(objectKey);

        return PhotoMapper.toDownloadPresignedUrlInfo(photo, fileName, url);
    }

    private String sanitizeFileName(String raw) {
        String name = raw == null ? "unnamed" : raw;
        name = name.replace('\\', '/'); // 구분자 통일
        int idx = name.lastIndexOf('/');
        if (idx >= 0) name = name.substring(idx + 1); // 경로 제거
        name = name.replaceAll("[^a-zA-Z0-9._-]", "_"); // 허용 문자 화이트리스트
        if (name.isBlank()) name = "unnamed";
        return name;
    }

    private void handleFailedUploads(User user, Long albumId, List<Long> failurePhotoIds) {
        int updatedRows = photoRepository.updateStatusByIdsAndUserIdAndExpectedStatus(
                failurePhotoIds,
                user.getId(),
                PhotoStatus.FAILED,
                PhotoStatus.UPLOADING
        );

        if (updatedRows != failurePhotoIds.size()) {
            throw new PhotoException(PhotoErrorCode.PHOTO_STATUS_UPDATE_FAILED);
        }

        // 업로드 로직 재설계 (선제적으로 count를 올려놓지 않음 -> count 감소 로직 제거)
        // 혹시 몰라서 나둠
//        if (updatedRows > 0) {
//            int decremented = albumRepository.decrementPhotoCount(albumId, updatedRows);
//            if (decremented == 0) {
//                throw new PhotoException(PhotoErrorCode.PHOTO_COUNT_DECREMENT_FAILED);
//            }
//            userService.decrementPhotoCount(user.getId(), updatedRows);
//        }
    }
}
