package com.cheeeese.album.application.validator;

import com.cheeeese.album.domain.Album;
import com.cheeeese.album.domain.type.Role;
import com.cheeeese.album.dto.request.AlbumCreationRequest;
import com.cheeeese.album.exception.AlbumException;
import com.cheeeese.album.exception.code.AlbumErrorCode;
import com.cheeeese.album.infrastructure.persistence.AlbumRepository;
import com.cheeeese.album.infrastructure.persistence.UserAlbumRepository;
import com.cheeeese.photo.domain.Photo;
import com.cheeeese.photo.exception.PhotoException;
import com.cheeeese.photo.exception.code.PhotoErrorCode;
import com.cheeeese.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AlbumValidator {

    private static final ZoneOffset KST_ZONE = ZoneOffset.of("+09:00");
    private final AlbumRepository albumRepository;
    private final UserAlbumRepository userAlbumRepository;

    public void validateAlbumCreation(long createdThisWeek, AlbumCreationRequest request) {
        if (request.themeEmoji() == null || request.themeEmoji().isBlank()) {
            throw new AlbumException(AlbumErrorCode.ALBUM_THEME_EMOJI_NOT_SELECTED);
        }

        if (request.title() == null || request.title().isBlank()) {
            throw new AlbumException(AlbumErrorCode.ALBUM_TITLE_REQUIRED);
        }

        if (request.eventDate() == null) {
            throw new AlbumException(AlbumErrorCode.ALBUM_EVENT_DATE_REQUIRED);
        }

        if (request.eventDate().isAfter(LocalDate.now(KST_ZONE))) {
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

    public void validateAlbumEntry(Album album, User user) {
        // 1. 앨범 만료 확인
        validateAlbumExpiration(album);

        // 2. 블랙리스트 확인 (권한 체크)
        validateUserBlacklisted(album, user);
    }

    public void validateAlbumCapacity(Album album) {
        int current = album.getCurrentParticipant();
        int max = album.getParticipant();

        if (current >= max) {
            throw new AlbumException(AlbumErrorCode.ALBUM_MAX_PARTICIPANT_REACHED);
        }
    }

    public void validateDownloadPermission(Album album, User user, List<Photo> photos) {
        validateAlbumParticipant(album, user);

        boolean existsPhotoInAlbum = photos.stream().allMatch(photo -> photo.getAlbum().getId().equals(album.getId()));

        if (!existsPhotoInAlbum) {
            throw new PhotoException(PhotoErrorCode.PHOTO_NOT_FOUND_IN_ALBUM);
        }
    }

    public void validateAlbumParticipant(Album album, User user) {
        validateAlbumEntry(album, user);

        userAlbumRepository.findByUserIdAndAlbumId(user.getId(), album.getId())
                .orElseThrow(() -> new AlbumException(AlbumErrorCode.USER_NOT_PARTICIPANT));
    }

    private void validateAlbumExpiration(Album album) {
        if (album.isExpired()) {
            throw new AlbumException(AlbumErrorCode.ALBUM_EXPIRED);
        }
    }

    /**
     * 사용자가 앨범의 블랙리스트에 등록되어 있는지 확인합니다.
     */
    private void validateUserBlacklisted(Album album, User user) {
        userAlbumRepository.findByAlbumIdAndUserIdAndRole(album.getId(), user.getId(), Role.BLACK)
                .ifPresent(userAlbum -> {
                    throw new AlbumException(AlbumErrorCode.USER_IS_BLACKLISTED);
                });
    }
}