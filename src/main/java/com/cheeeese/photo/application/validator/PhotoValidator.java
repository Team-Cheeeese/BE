package com.cheeeese.photo.application.validator;

import com.cheeeese.album.domain.Album;
import com.cheeeese.album.domain.UserAlbum;
import com.cheeeese.album.domain.type.Role;
import com.cheeeese.photo.domain.Photo;
import com.cheeeese.photo.dto.request.PhotoPresignedUrlRequest;
import com.cheeeese.photo.exception.PhotoException;
import com.cheeeese.photo.exception.code.PhotoErrorCode;
import com.cheeeese.photo.infrastructure.persistence.PhotoRepository;
import com.cheeeese.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class PhotoValidator {

    private final PhotoRepository photoRepository;

    private static final long MAX_FILE_SIZE = 13 * 1024 * 1024; // 13MB
    private static final List<String> ALLOWED_TYPES = List.of("image/jpeg", "image/png", "image/jpg");

    /**
     * presigned URL 발급 전, 파일 정보 형식/용량 검증
     */
    public void validateFileInfos(List<PhotoPresignedUrlRequest.FileInfo> fileInfos) {
        if (fileInfos == null || fileInfos.isEmpty()) {
            throw new PhotoException(PhotoErrorCode.PHOTO_FILE_LIST_EMPTY);
        }

        for (PhotoPresignedUrlRequest.FileInfo file : fileInfos) {
            if (file.fileName() == null || file.fileName().isBlank()) {
                throw new PhotoException(PhotoErrorCode.PHOTO_FILE_NAME_REQUIRED);
            }

            if (file.captureTime() == null) {
                throw new PhotoException(PhotoErrorCode.PHOTO_CAPTURE_TIME_REQUIRED);
            }

            if (file.fileSize() > MAX_FILE_SIZE) {
                throw new PhotoException(PhotoErrorCode.PHOTO_FILE_SIZE_EXCEEDED);
            }

            if (!ALLOWED_TYPES.contains(file.contentType())) {
                throw new PhotoException(PhotoErrorCode.PHOTO_INVALID_CONTENT_TYPE);
            }
        }
    }

    /**
     * 앨범 내 업로드 제한 검증
     */
    public void validatePhotoCount(long currentCount, int uploadCount, int maxPhotoCount) {
        if (currentCount + uploadCount > maxPhotoCount) {
            throw new PhotoException(PhotoErrorCode.PHOTO_MAX_COUNT_EXCEEDED);
        }
    }

    /**
     * 존재, 소유, 동일 앨범 검증을 통합 수행
     */
    public ValidatedPhotos validatePhotos(Long userId, List<Long> photoIds) {
        validatePhotoIdsNotEmpty(photoIds);
        List<Photo> photos = findAndValidateExistence(photoIds);
        validateOwnership(photos, userId);
        Long albumId = validateSingleAlbum(photos);
        return new ValidatedPhotos(photos, albumId);
    }

    /**
     * photoIds가 비어있지 않은지 검증
     */
    private void validatePhotoIdsNotEmpty(List<Long> photoIds) {
        if (photoIds == null || photoIds.isEmpty()) {
            throw new PhotoException(PhotoErrorCode.PHOTO_ID_LIST_EMPTY);
        }
    }

    /**
     * photoId 리스트 기반으로 존재하는 사진 조회 및 존재 검증
     */
    private List<Photo> findAndValidateExistence(List<Long> photoIds) {
        List<Photo> photos = photoRepository.findAllById(photoIds);

        Set<Long> uniqueRequestedIds = new HashSet<>(photoIds);

        Set<Long> foundIds = photos.stream()
                .map(Photo::getId)
                .collect(Collectors.toSet());

        Set<Long> missingIds = uniqueRequestedIds.stream()
                .filter(id -> !foundIds.contains(id))
                .collect(Collectors.toSet());

        if (!missingIds.isEmpty()) {
            log.error("존재하지 않는 photoIds: {}", missingIds);
            throw new PhotoException(PhotoErrorCode.PHOTO_NOT_FOUND);
        }
        return photos;
    }

    /**
     * 소유자 일치 검증
     */
    private void validateOwnership(List<Photo> photos, Long userId) {
        boolean invalidOwner = photos.stream()
                .anyMatch(photo -> !photo.getUser().getId().equals(userId));

        if (invalidOwner) {
            throw new PhotoException(PhotoErrorCode.PHOTO_OWNER_MISMATCH);
        }
    }

    /**
     * 동일 앨범 검증
     */
    private Long validateSingleAlbum(List<Photo> photos) {
        Set<Long> albumIds = photos.stream()
                .map(photo -> photo.getAlbum().getId())
                .collect(Collectors.toSet());

        if (albumIds.size() != 1) {
            throw new PhotoException(PhotoErrorCode.PHOTO_REPORT_INVALID_ALBUM);
        }

        return albumIds.iterator().next();
    }

    /**
     * 검증 결과 객체: 앨범 ID + 사진 리스트 보관
     */
    public record ValidatedPhotos(List<Photo> photos, Long albumId) {}

    /**
     * 사진 삭제 권한 검증
     */
    public void validateDeletePermission(User user, UserAlbum userAlbum, Album album, Photo photo) {
        if (userAlbum.getRole() == Role.MAKER) return;

        if (!photo.getUser().getId().equals(user.getId())) {
            throw new PhotoException(PhotoErrorCode.PHOTO_OWNER_MISMATCH);
        }

        if (!photo.getAlbum().getId().equals(album.getId())) {
            throw new PhotoException(PhotoErrorCode.PHOTO_NOT_FOUND_IN_ALBUM);
        }
    }

    public void validateNotRecentlyDownloaded(Set<Long> recentDownloadIds) {
        if (!recentDownloadIds.isEmpty()) {
            throw new PhotoException(PhotoErrorCode.PHOTO_RECENT_DOWNLOAD_BLOCKED);
        }
    }
}
