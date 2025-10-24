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
import com.cheeeese.photo.infrastructure.mapper.PhotoMapper;
import com.cheeeese.photo.infrastructure.persistence.PhotoRepository;
import com.cheeeese.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PhotoService {

    private final PhotoRepository photoRepository;
    private final PhotoValidator photoValidator;
    private final AlbumValidator albumValidator;
    private final AlbumRepository albumRepository;
    private final PresignedUrlService presignedUrlService;

    private static final String ORIGINAL_PHOTO_PATH_FORMAT = "album/%d/original/%d_%s";

    public long countTotalPhotos(Long albumId) {
        return photoRepository.countByAlbumIdAndIsDeletedFalse(albumId);
    }

    public List<String> getRecentPhotoUrls(Long albumId) {
        List<Photo> recentPhotos = photoRepository.findTop9ByAlbumIdAndIsDeletedFalseOrderByCreatedAtDesc(albumId);
        return recentPhotos.stream()
                .map(Photo::getImageUrl)
                .collect(Collectors.toList());
    }

    @Transactional
    public PhotoPresignedUrlResponse createPresignedUrls(User user, PhotoPresignedUrlRequest request) {
        Album album = validateAlbumAndPermission(user, request.albumCode());
        validateUploadRequest(album, request);

        List<PhotoPresignedUrlResponse.PresignedUrlInfo> presignedUrls = generatePresignedUrls(user, album, request.fileInfos());
        albumRepository.incrementPhotoCount(album.getId(), request.fileInfos().size());

        return PhotoMapper.toPresignedUrlResponse(presignedUrls);
    }

    @Transactional
    public void reportUploadResult(User user, PhotoUploadReportRequest request) {
        PhotoValidator.ValidatedPhotos validated = validateRequestAndPhotos(user, request);
        Long albumId = validated.albumId();

        handleSuccessfulUploads(user.getId(), request.successPhotoIds());

        handleFailedUploads(user.getId(), albumId, request.failurePhotoIds());
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
        Photo photo = PhotoMapper.toEntity(user.getId(), album.getId());
        photoRepository.save(photo);

        String objectKey = String.format(
                ORIGINAL_PHOTO_PATH_FORMAT,
                album.getId(),
                photo.getId(),
                file.fileName()
        );

        String uploadUrl = presignedUrlService.generatePresignedPutUrl(objectKey, file.contentType());
        photo.updateImageUrl(objectKey);

        return PhotoMapper.toPresignedUrlInfo(photo.getId(), uploadUrl);
    }

    private PhotoValidator.ValidatedPhotos validateRequestAndPhotos(User user, PhotoUploadReportRequest request) {
        List<Long> allPhotoIds = Stream.concat(
                request.successPhotoIds().stream(),
                request.failurePhotoIds().stream()
        ).toList();

        return photoValidator.validatePhotos(user.getId(), allPhotoIds);
    }

    private void handleSuccessfulUploads(Long userId, List<Long> successPhotoIds) {
        if (successPhotoIds == null || successPhotoIds.isEmpty()) {
            return;
        }

        photoRepository.updateStatusByIdsAndUserIdAndExpectedStatus(
                successPhotoIds,
                userId,
                PhotoStatus.PROCESSING,
                PhotoStatus.UPLOADING
        );
    }

    private void handleFailedUploads(Long userId, Long albumId, List<Long> failurePhotoIds) {
        if (failurePhotoIds == null || failurePhotoIds.isEmpty()) {
            return;
        }

        photoRepository.updateStatusByIdsAndUserIdAndExpectedStatus(
                failurePhotoIds,
                userId,
                PhotoStatus.FAILED,
                PhotoStatus.UPLOADING
        );

        albumRepository.decrementPhotoCount(albumId, failurePhotoIds.size());
    }


}
