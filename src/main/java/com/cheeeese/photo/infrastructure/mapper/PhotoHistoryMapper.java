package com.cheeeese.photo.infrastructure.mapper;

import com.cheeeese.photo.domain.Photo;
import com.cheeeese.photo.domain.PhotoHistory;
import com.cheeeese.user.domain.User;

public class PhotoHistoryMapper {

    public static PhotoHistory toEntity(User user, Photo photo) {
        return PhotoHistory.builder()
                .user(user)
                .photo(photo)
                .build();
    }
}
