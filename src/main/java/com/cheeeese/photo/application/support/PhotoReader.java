package com.cheeeese.photo.application.support;

import com.cheeeese.photo.domain.Photo;
import com.cheeeese.photo.exception.PhotoException;
import com.cheeeese.photo.exception.code.PhotoErrorCode;
import com.cheeeese.photo.infrastructure.persistence.PhotoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PhotoReader {

    private final PhotoRepository photoRepository;

    public Photo getPhoto(Long photoId) { // TODO: 추후 삭제
        Photo photo = photoRepository.findById(photoId)
                .orElseThrow(() -> new PhotoException(PhotoErrorCode.PHOTO_NOT_FOUND));

        if (photo.isDeleted()) {
            throw new PhotoException(PhotoErrorCode.PHOTO_ALREADY_DELETED);
        }

        return photo;
    }

    public Photo getPhotoInAlbum(Long photoId, String albumCode) {
        Photo photo = photoRepository.findByIdAndAlbum_Code(photoId, albumCode)
                .orElseThrow(() -> new PhotoException(PhotoErrorCode.PHOTO_NOT_FOUND));

        if (photo.isDeleted()) {
            throw new PhotoException(PhotoErrorCode.PHOTO_ALREADY_DELETED);
        }

        return photo;
    }
}
