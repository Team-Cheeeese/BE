package com.cheeeese.album.application;

import com.cheeeese.album.application.validator.AlbumValidator;
import com.cheeeese.album.domain.Album;
import com.cheeeese.album.dto.response.AlbumInvitationResponse;
import com.cheeeese.album.infrastructure.mapper.AlbumMapper;
import com.cheeeese.user.domain.User;
import com.cheeeese.user.exception.UserException;
import com.cheeeese.user.exception.code.UserErrorCode;
import com.cheeeese.user.infrastructure.persistence.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AlbumService {

    private final AlbumValidator albumValidator;
    private final UserRepository userRepository;

    public AlbumInvitationResponse getInvitationInfo(String code) {
        Album album = albumValidator.validateAlbumCode(code);

        albumValidator.validateAlbumExpiration(album);

        User host = userRepository.findById(album.getHostId())
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        return AlbumMapper.toInvitationResponse(album, host);
    }
}
