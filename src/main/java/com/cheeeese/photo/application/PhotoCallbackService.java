package com.cheeeese.photo.application;

import com.cheeeese.photo.domain.PhotoStatus;
import com.cheeeese.photo.dto.request.PhotoCompleteRequest;
import com.cheeeese.photo.exception.PhotoException;
import com.cheeeese.photo.exception.code.PhotoErrorCode;
import com.cheeeese.photo.infrastructure.persistence.PhotoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class PhotoCallbackService {

    private final PhotoRepository photoRepository;
    private final PhotoQueryService photoQueryService;

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

        String albumCode = photoRepository.findAlbumCodeByPhotoId(request.photoId());
        photoQueryService.invalidatePhotoCache(albumCode);
    }
}
