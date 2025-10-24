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
    public PhotoPresignedUrlResponse createPresignedUrls(
            User user,
            PhotoPresignedUrlRequest request
    ) {
        Album album = albumValidator.validateAlbumCode(request.albumCode());
        albumValidator.validateUploadPermission(album, user);

        int currentCount = album.getCurrentPhotoCount();
        int maxCount = album.getMaxPhotoCount();
        int requestedCount = request.fileInfos().size();

        photoValidator.validatePhotoCount(currentCount, requestedCount, maxCount);
        photoValidator.validateFileInfos(request.fileInfos());

        List<PhotoPresignedUrlResponse.PresignedUrlInfo> presignedUrls =
                request.fileInfos().stream()
                        .map(file -> {
                            Photo photo = PhotoMapper.toEntity(
                                    user.getId(),
                                    album.getId()
                            );
                            photoRepository.save(photo);

                            String objectKey = String.format(
                                    "album/%d/original/%d_%s",
                                    album.getId(),
                                    photo.getId(),
                                    file.fileName()
                            );

                            String uploadUrl = presignedUrlService.generatePresignedPutUrl(
                                    objectKey,
                                    file.contentType()
                            );

                            photo.updateImageUrl(objectKey);

                            return PhotoMapper.toPresignedUrlInfo(photo.getId(), uploadUrl);
                        })
                        .collect(Collectors.toList());

        albumRepository.incrementPhotoCount(album.getId(), requestedCount);

        return PhotoMapper.toPresignedUrlResponse(presignedUrls);
    }

    @Transactional
    public void reportUploadResult(User user, PhotoUploadReportRequest request) {
        List<Long> allPhotoIds = Stream.concat(
                request.successPhotoIds().stream(),
                request.failurePhotoIds().stream()
        ).toList();

        PhotoValidator.ValidatedPhotos validated = photoValidator.validatePhotos(user.getId(), allPhotoIds);
        Long albumId = validated.albumId();


        if (!request.successPhotoIds().isEmpty()) {
            photoRepository.updateStatusByIdsAndUserIdAndExpectedStatus(
                    request.successPhotoIds(),
                    user.getId(),
                    PhotoStatus.PROCESSING,
                    PhotoStatus.UPLOADING
            );
        }

        if (!request.failurePhotoIds().isEmpty()) {
            photoRepository.updateStatusByIdsAndUserIdAndExpectedStatus(
                    request.failurePhotoIds(),
                    user.getId(),
                    PhotoStatus.FAILED,
                    PhotoStatus.UPLOADING
            );
            albumRepository.decrementPhotoCount(albumId, request.failurePhotoIds().size());
        }
    }

}
