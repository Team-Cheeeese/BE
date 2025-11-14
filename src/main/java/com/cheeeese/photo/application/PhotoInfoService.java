package com.cheeeese.photo.application;

import com.cheeeese.album.application.validator.AlbumValidator;
import com.cheeeese.album.domain.Album;
import com.cheeeese.album.domain.type.Role;
import com.cheeeese.global.security.CurrentUserProvider;
import com.cheeeese.photo.domain.Photo;
import com.cheeeese.photo.dto.response.PhotoLikedUserResponse;
import com.cheeeese.photo.exception.PhotoException;
import com.cheeeese.photo.exception.code.PhotoErrorCode;
import com.cheeeese.photo.infrastructure.mapper.PhotoMapper;
import com.cheeeese.photo.infrastructure.persistence.PhotoLikesRepository;
import com.cheeeese.photo.infrastructure.persistence.PhotoRepository;
import com.cheeeese.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PhotoInfoService {

    private final CurrentUserProvider currentUserProvider;
    private final PhotoRepository photoRepository;
    private final PhotoLikesRepository photoLikesRepository;
    private final AlbumValidator albumValidator;

    public PhotoLikedUserResponse getPhotoLikedUsers(User user, String code, Long photoId) {
        Album album = albumValidator.validateAlbumCode(code);

        albumValidator.validateAlbumParticipant(album, user);

        Photo photo = photoRepository.findByIdAndAlbum_Code(photoId, code)
                .orElseThrow(() -> new PhotoException(PhotoErrorCode.PHOTO_NOT_FOUND));

        List<User> users = photoLikesRepository.findLikersByPhotoId(photo.getId());

        List<PhotoLikedUserResponse.PhotoLiker> likers = users.stream()
                .map(liker -> {
                    boolean isMe = liker.getId().equals(user.getId());
                    Role role = liker.getId().equals(album.getMakerId()) ? Role.MAKER : Role.GUEST;
                    return PhotoMapper.toPhotoLiker(liker, isMe, role);
                })
                .toList();

        return PhotoMapper.toPhotoLikerResponse(photo, likers);
    }
}
