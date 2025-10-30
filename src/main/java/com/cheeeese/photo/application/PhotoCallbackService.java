package com.cheeeese.photo.application;

import com.cheeeese.photo.domain.PhotoStatus;
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

    public void markUploadCompleted(Long photoId) {
        int updated = photoRepository.updateStatus(photoId, PhotoStatus.COMPLETED);
        if (updated == 0) {
            throw new PhotoException(PhotoErrorCode.PHOTO_ID_NOT_FOUND);
        }
    }
}
