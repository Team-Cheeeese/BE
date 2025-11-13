package com.cheeeese.photo.application;

import com.cheeeese.album.application.validator.AlbumValidator;
import com.cheeeese.album.domain.Album;
import com.cheeeese.album.domain.type.Role;
import com.cheeeese.global.security.CurrentUserProvider;
import com.cheeeese.photo.domain.Photo;
import com.cheeeese.album.dto.response.AlbumInfoResponse;
import com.cheeeese.photo.dto.response.PhotoLikerResponse;
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

    public PhotoLikerResponse getPhotoLikers(User user, String code, Long photoId) {
        Album album = albumValidator.validateAlbumCode(code);

        albumValidator.validateAlbumParticipant(album, user);

        Photo photo = photoRepository.findById(photoId)
                .orElseThrow(() -> new PhotoException(PhotoErrorCode.PHOTO_NOT_FOUND));

        List<User> users = photoLikesRepository.findLikersByPhotoId(photo.getId());

        List<PhotoLikerResponse.PhotoLiker> likers = users.stream()
                .map(userList -> {
                    boolean isMe = user.getId().equals(currentUserProvider.getCurrentUser().getId());
                    Role role = user.getId().equals(album.getMakerId()) ? Role.MAKER : Role.GUEST;
                    return PhotoMapper.toPhotoLiker(user, isMe, role);
                })
                .toList();

        return PhotoMapper.toPhotoLikerResponse(photo, likers);
    }
}
