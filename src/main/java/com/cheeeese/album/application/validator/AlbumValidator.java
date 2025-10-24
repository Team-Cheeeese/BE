package com.cheeeese.album.application.validator;

import com.cheeeese.album.domain.Album;
import com.cheeeese.album.dto.request.AlbumCreationRequest;
import com.cheeeese.album.exception.AlbumException;
import com.cheeeese.album.exception.code.AlbumErrorCode;
import com.cheeeese.album.infrastructure.persistence.AlbumRepository;
import com.cheeeese.album.infrastructure.persistence.AlbumParticipantRepository;
import com.cheeeese.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class AlbumValidator {

    private final AlbumRepository albumRepository;
    private final AlbumParticipantRepository albumParticipantRepository;

    public void validateAlbumCreation(long createdThisWeek, AlbumCreationRequest request) {
        if (!request.isTermsAgreement()) {
            throw new AlbumException(AlbumErrorCode.ALBUM_REQUIRED_TERMS_NOT_AGREED);
        }

        if (request.themeEmoji() == null || request.themeEmoji().isBlank()) {
            throw new AlbumException(AlbumErrorCode.ALBUM_THEME_EMOJI_NOT_SELECTED);
        }

        if (request.title() == null || request.title().isBlank()) {
            throw new AlbumException(AlbumErrorCode.ALBUM_TITLE_REQUIRED);
        }

        if (request.eventDate() == null) {
            throw new AlbumException(AlbumErrorCode.ALBUM_EVENT_DATE_REQUIRED);
        }

        if (request.eventDate().isAfter(LocalDate.now())) {
            throw new AlbumException(AlbumErrorCode.ALBUM_EVENT_DATE_INVALID);
        }

        if (request.participant() < 1 || request.participant() > 64) {
            throw new AlbumException(AlbumErrorCode.ALBUM_INVALID_CAPACITY);
        }

        if (createdThisWeek >= 3) {
            throw new AlbumException(AlbumErrorCode.ALBUM_CREATION_LIMIT_EXCEEDED);
        }
    }

    public Album validateAlbumCode(String code) {
        return albumRepository.findByCode(code)
                .orElseThrow(() -> new AlbumException(AlbumErrorCode.ALBUM_NOT_FOUND));
    }

    public void validateAlbumExpiration(Album album) {
        if (album.isExpired()) {
            throw new AlbumException(AlbumErrorCode.ALBUM_EXPIRED);
        }
    }

    public void validateAlbumEntry(Album album, User user) {
        // 1. 앨범 만료 확인
        validateAlbumExpiration(album);

        // 2. 블랙리스트 확인 (권한 체크)
        validateUserBlacklisted(album, user);
    }

    public void validateUploadPermission(Album album, User user) { // [NEW]
        validateAlbumExpiration(album);

        validateUserBlacklisted(album, user);

        albumParticipantRepository.findByUserIdAndAlbumId(user.getId(), album.getId())
                .orElseThrow(() -> new AlbumException(AlbumErrorCode.USER_NOT_PARTICIPANT));
    }

    /**
     * 사용자가 앨범의 블랙리스트에 등록되어 있는지 확인합니다.
     */
    private void validateUserBlacklisted(Album album, User user) {
        albumParticipantRepository.findByAlbumIdAndUserIdAndIsBlacklistedTrue(album.getId(), user.getId())
                .ifPresent(blacklisted -> {
                    throw new AlbumException(AlbumErrorCode.USER_IS_BLACKLISTED);
                });
    }
}