package com.cheeeese.photo.application;

import com.cheeeese.album.application.validator.AlbumValidator;
import com.cheeeese.album.domain.Album;
import com.cheeeese.photo.domain.Photo;
import com.cheeeese.photo.dto.response.PhotoInfoResponse;
import com.cheeeese.photo.exception.PhotoException;
import com.cheeeese.photo.exception.code.PhotoErrorCode;
import com.cheeeese.photo.infrastructure.mapper.PhotoMapper;
import com.cheeeese.photo.infrastructure.persistence.PhotoRepository;
import com.cheeeese.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PhotoInfoService {

    private final PhotoRepository photoRepository;
    private final AlbumValidator albumValidator;

    public PhotoInfoResponse getPhotoInfo(User user, String code, Long photoId) {
        Album album = albumValidator.validateAlbumCode(code);

        albumValidator.validateAlbumParticipant(album, user);

        Photo photo = photoRepository.findById(photoId)
                .orElseThrow(() -> new PhotoException(PhotoErrorCode.PHOTO_NOT_FOUND));

        return PhotoMapper.toPhotoInfoResponse(photo);
    }
}
