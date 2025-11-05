package com.cheeeese.photo.infrastructure.mapper;

import com.cheeeese.photo.domain.Photo;
import com.cheeeese.photo.domain.PhotoLikes;
import com.cheeeese.user.domain.User;

public class PhotoLikesMapper {

    public static PhotoLikes toEntity(User user, Photo photo) {
        return PhotoLikes.builder()
                .user(user)
                .photo(photo)
                .build();
    }
}
