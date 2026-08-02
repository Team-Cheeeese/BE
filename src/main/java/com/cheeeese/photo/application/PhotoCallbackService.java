package com.cheeeese.photo.application;

import com.cheeeese.album.infrastructure.persistence.AlbumRepository;
import com.cheeeese.photo.application.logger.PhotoLogger;
import com.cheeeese.photo.domain.Photo;
import com.cheeeese.photo.domain.PhotoStatus;
import com.cheeeese.photo.dto.request.PhotoCompleteRequest;
import com.cheeeese.photo.exception.PhotoException;
import com.cheeeese.photo.exception.code.PhotoErrorCode;
import com.cheeeese.photo.infrastructure.persistence.PhotoRepository;
import com.cheeeese.user.application.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class PhotoCallbackService {

    private final PhotoRepository photoRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final AlbumRepository albumRepository;
    private final UserService userService;
    private final PhotoLogger photoLogger;

    public void markUploadCompleted(PhotoCompleteRequest request) {
        int updated = photoRepository.updateStatusAndUrl(
                request.photoId(),
                PhotoStatus.UPLOADING,
                PhotoStatus.COMPLETED,
                request.thumbnailUrl()
        );

        if (updated == 0) {
            throw new PhotoException(PhotoErrorCode.THUMBNAIL_UPDATE_FAILED);
        }

        Photo photo = photoRepository.findById(request.photoId())
                .orElseThrow(() -> new PhotoException(PhotoErrorCode.PHOTO_NOT_FOUND));

        Long albumId = photo.getAlbum().getId();

        int albumUpdated = albumRepository.incrementPhotoCount(photo.getAlbum().getId(), 1);
        if (albumUpdated == 0) {
            photoRepository.updateStatusAndUrl(
                    photo.getId(),
                    PhotoStatus.COMPLETED,
                    PhotoStatus.FAILED,
                    photo.getThumbnailUrl()
            );
            throw new PhotoException(PhotoErrorCode.PHOTO_COUNT_INCREMENT_FAILED);
        }
        int updatedPhotoCount = albumRepository.findCurrentPhotoCountById(albumId);

        userService.incrementPhotoCount(photo.getUser().getId(), 1);

        String albumCode = photoRepository.findAlbumCodeByPhotoId(request.photoId());

        photoLogger.logUploadCompleted(photo.getUser().getId(), albumCode, updatedPhotoCount, photo.getId());

        if (albumCode != null) {
            eventPublisher.publishEvent(new PhotoCacheInvalidationEvent(albumCode));
        }
    }
}
