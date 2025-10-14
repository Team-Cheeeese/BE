package com.cheeeese.album.application.validator;

import com.cheeeese.album.domain.Album;
import com.cheeeese.album.domain.UserAlbumRole;
import com.cheeeese.album.exception.AlbumException;
import com.cheeeese.album.exception.code.AlbumErrorCode;
import com.cheeeese.album.infrastructure.persistence.AlbumRepository;
import com.cheeeese.album.infrastructure.persistence.UserAlbumRepository;
import com.cheeeese.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AlbumValidator {

    private final AlbumRepository albumRepository;
    private final UserAlbumRepository userAlbumRepository;

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

        // 3. 정원 초과 확인 (신규 참여자에게만 적용)
        validateAlbumCapacity(album, user);
    }

    /**
     * 사용자가 앨범의 블랙리스트에 등록되어 있는지 확인합니다.
     */
    private void validateUserBlacklisted(Album album, User user) {
        userAlbumRepository.findByAlbumIdAndUserIdAndRole(album.getId(), user.getId(), UserAlbumRole.BLACK)
                .ifPresent(userAlbum -> {
                    throw new AlbumException(AlbumErrorCode.USER_IS_BLACKLISTED);
                });
    }

    /**
     * 신규 참여자일 경우, 앨범의 최대 정원 초과 여부를 확인합니다.
     */
    private void validateAlbumCapacity(Album album, User user) {
        // 이미 참여 중인 사용자는 정원 체크를 건너뜁니다.
        boolean isAlreadyParticipant = userAlbumRepository.findByUserIdAndAlbumId(user.getId(), album.getId()).isPresent();

        if (!isAlreadyParticipant && album.isFull()) {
            throw new AlbumException(AlbumErrorCode.ALBUM_MAX_PARTICIPANT_REACHED);
        }
    }
}