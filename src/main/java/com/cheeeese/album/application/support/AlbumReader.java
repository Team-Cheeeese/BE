package com.cheeeese.album.application.support;

import com.cheeeese.album.domain.Album;
import com.cheeeese.album.domain.UserAlbum;
import com.cheeeese.album.domain.type.Role;
import com.cheeeese.album.exception.AlbumException;
import com.cheeeese.album.exception.code.AlbumErrorCode;
import com.cheeeese.album.infrastructure.persistence.AlbumRepository;
import com.cheeeese.album.infrastructure.persistence.UserAlbumRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AlbumReader {

    private final AlbumRepository albumRepository;
    private final UserAlbumRepository userAlbumRepository;

    public Album getAlbum(Long albumId) {
        return albumRepository.findById(albumId)
                .orElseThrow(() -> new AlbumException(AlbumErrorCode.ALBUM_NOT_FOUND));
    }

    public UserAlbum getAlbumParticipant(Long userId, Long albumId) {
        return userAlbumRepository.findByUserIdAndAlbumId(userId, albumId)
                .orElseThrow(() -> new AlbumException(AlbumErrorCode.USER_NOT_PARTICIPANT));
    }

    public List<UserAlbum> getAlbumParticipants(Long albumId) {
        return userAlbumRepository.findNotificationParticipants(albumId, Role.BLACK);
    }
}
