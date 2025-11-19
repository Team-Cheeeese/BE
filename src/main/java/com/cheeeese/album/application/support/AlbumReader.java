package com.cheeeese.album.application.support;

import com.cheeeese.album.domain.Album;
import com.cheeeese.album.domain.UserAlbum;
import com.cheeeese.album.exception.AlbumException;
import com.cheeeese.album.exception.code.AlbumErrorCode;
import com.cheeeese.album.infrastructure.persistence.UserAlbumRepository;
import com.cheeeese.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AlbumReader {

    private final UserAlbumRepository userAlbumRepository;

    public UserAlbum getAlbumParticipant(Album album, User user) {
        return userAlbumRepository.findByUserIdAndAlbumId(user.getId(), album.getId())
                .orElseThrow(() -> new AlbumException(AlbumErrorCode.USER_NOT_PARTICIPANT));
    }
}
