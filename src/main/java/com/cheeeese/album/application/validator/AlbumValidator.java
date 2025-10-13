package com.cheeeese.album.application.validator;

import com.cheeeese.album.domain.Album;
import com.cheeeese.album.exception.AlbumException;
import com.cheeeese.album.exception.code.AlbumErrorCode;
import com.cheeeese.album.infrastructure.persistence.AlbumRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AlbumValidator {

    private final AlbumRepository albumRepository;

    public Album validateAlbumCode(String code) {
        return albumRepository.findByCode(code)
                .orElseThrow(() -> new AlbumException(AlbumErrorCode.ALBUM_NOT_FOUND));
    }

    public void validateAlbumExpiration(Album album) {
        if (album.isExpired()) {
            throw new AlbumException(AlbumErrorCode.ALBUM_EXPIRED);
        }
    }
}