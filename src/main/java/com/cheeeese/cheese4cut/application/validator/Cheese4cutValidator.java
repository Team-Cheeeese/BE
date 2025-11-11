package com.cheeeese.cheese4cut.application.validator;

import com.cheeeese.album.domain.Album;
import com.cheeeese.album.domain.type.Role;
import com.cheeeese.album.exception.AlbumException;
import com.cheeeese.album.exception.code.AlbumErrorCode;
import com.cheeeese.album.infrastructure.persistence.UserAlbumRepository;
import com.cheeeese.cheese4cut.exception.Cheese4cutException;
import com.cheeeese.cheese4cut.exception.code.Cheese4cutErrorCode;
import com.cheeeese.photo.domain.Photo;
import com.cheeeese.photo.domain.PhotoStatus;
import com.cheeeese.photo.exception.PhotoException;
import com.cheeeese.photo.exception.code.PhotoErrorCode;
import com.cheeeese.photo.infrastructure.persistence.PhotoRepository;
import com.cheeeese.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;

@Component
@RequiredArgsConstructor
public class Cheese4cutValidator {

    private final UserAlbumRepository userAlbumRepository;
    private final PhotoRepository photoRepository;

    public void validateUserIsMaker(Album album, User user) {
        boolean isMaker = userAlbumRepository.findByAlbumIdAndUserIdAndRole(album.getId(), user.getId(), Role.MAKER).isPresent();
        if (!isMaker) {
            throw new AlbumException(AlbumErrorCode.USER_NOT_MAKER);
        }
    }

    public void validateFinalizePhotos(Album album, List<Long> photoIds) {
        if (new HashSet<>(photoIds).size() != 4) {
            throw new Cheese4cutException(Cheese4cutErrorCode.CHEESE4CUT_INVALID_PHOTO_COUNT);
        }

        List<Photo> photos = photoRepository.findAllById(photoIds);

        if (photos.size() != 4) {
            throw new PhotoException(PhotoErrorCode.PHOTO_NOT_FOUND);
        }

        boolean allValid = photos.stream().allMatch(photo ->
                photo.getAlbum().getId().equals(album.getId()) && photo.getStatus() == PhotoStatus.COMPLETED
        );

        if (!allValid) {
            throw new Cheese4cutException(Cheese4cutErrorCode.CHEESE4CUT_PHOTO_INVALID_STATUS_OR_ALBUM);
        }
    }
}