package com.cheeeese.photo.application;

import com.cheeeese.album.application.validator.AlbumValidator;
import com.cheeeese.album.domain.Album;
import com.cheeeese.album.domain.type.Role;
import com.cheeeese.global.util.ProfileImageUtil;
import com.cheeeese.global.util.resolver.CdnUrlResolver;
import com.cheeeese.photo.application.support.PhotoReader;
import com.cheeeese.photo.domain.Photo;
import com.cheeeese.photo.dto.response.PhotoLikedUserResponse;
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

    private final PhotoRepository photoRepository;
    private final PhotoLikesRepository photoLikesRepository;
    private final PhotoReader photoReader;
    private final AlbumValidator albumValidator;
    private final CdnUrlResolver cdnUrlResolver;

    public PhotoLikedUserResponse getPhotoLikedUsers(User user, String code, Long photoId) {
        Album album = albumValidator.validateAlbumCode(code);

        albumValidator.validateAlbumParticipant(album, user);

        Photo photo = photoReader.getPhotoInAlbum(photoId, code);

        List<User> users = photoLikesRepository.findLikersByPhotoId(photo.getId());

        List<PhotoLikedUserResponse.PhotoLiker> likers = users.stream()
                .map(liker -> {
                    String profileImage = ProfileImageUtil.resolveProfileImage(liker, cdnUrlResolver);
                    boolean isMe = liker.getId().equals(user.getId());
                    Role role = liker.getId().equals(album.getMakerId()) ? Role.MAKER : Role.GUEST;

                    return PhotoMapper.toPhotoLiker(liker, profileImage, isMe, role);
                })
                .toList();

        return PhotoMapper.toPhotoLikerResponse(photo, likers);
    }
}
