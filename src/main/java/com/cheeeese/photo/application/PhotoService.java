package com.cheeeese.photo.application;

import com.cheeeese.album.application.validator.AlbumValidator;
import com.cheeeese.album.domain.Album;
import com.cheeeese.album.infrastructure.persistence.AlbumRepository;
import com.cheeeese.photo.application.validator.PhotoValidator;
import com.cheeeese.photo.domain.Photo;
import com.cheeeese.photo.domain.PhotoStatus;
import com.cheeeese.photo.dto.request.PhotoPresignedUrlRequest;
import com.cheeeese.photo.dto.request.PhotoUploadReportRequest;
import com.cheeeese.photo.dto.response.PhotoPresignedUrlResponse;
import com.cheeeese.photo.exception.PhotoException;
import com.cheeeese.photo.exception.code.PhotoErrorCode;
import com.cheeeese.photo.infrastructure.mapper.PhotoMapper;
import com.cheeeese.photo.infrastructure.persistence.PhotoRepository;
import com.cheeeese.user.application.UserService;
import com.cheeeese.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PhotoService {

    private final UserService userService;
    private final PhotoRepository photoRepository;
    private final PhotoValidator photoValidator;
    private final AlbumValidator albumValidator;
    private final AlbumRepository albumRepository;
    private final PresignedUrlService presignedUrlService;

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
        validateUploadRequest(album, request);

        int uploadCount = request.fileInfos().size();

        int updatedRows = albumRepository.incrementPhotoCount(album.getId(), uploadCount);
        if (updatedRows != 1) {
            throw new PhotoException(PhotoErrorCode.PHOTO_COUNT_INCREMENT_FAILED);
        }

        userService.incrementPhotoCount(user.getId(), uploadCount);

        List<PhotoPresignedUrlResponse.PresignedUrlInfo> presignedUrls = generatePresignedUrls(user, album, request.fileInfos());
        return PhotoMapper.toPresignedUrlResponse(presignedUrls);
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

    private Album validateAlbumAndPermission(User user, String albumCode) {
        Album album = albumValidator.validateAlbumCode(albumCode);
        albumValidator.validateUploadPermission(album, user);
        return album;
    }

    private void validateUploadRequest(Album album, PhotoPresignedUrlRequest request) {
        int currentCount = album.getCurrentPhotoCount();
        int maxCount = album.getMaxPhotoCount();
        int requestedCount = request.fileInfos().size();

        photoValidator.validatePhotoCount(currentCount, requestedCount, maxCount);
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

        if (updatedRows > 0) {
            int decremented = albumRepository.decrementPhotoCount(albumId, updatedRows);
            if (decremented == 0) {
                throw new PhotoException(PhotoErrorCode.PHOTO_COUNT_DECREMENT_FAILED);
            }
            userService.decrementPhotoCount(user.getId(), updatedRows);
        }
    }


}
