package com.cheeeese.album.application;

import com.cheeeese.album.application.validator.AlbumValidator;
import com.cheeeese.album.domain.Album;
import com.cheeeese.album.dto.response.AlbumInvitationResponse;
import com.cheeeese.album.infrastructure.mapper.AlbumMapper;
import com.cheeeese.photo.application.PhotoService;
import com.cheeeese.album.domain.UserAlbum;
import com.cheeeese.album.dto.response.AlbumEnterResponse;
import com.cheeeese.album.dto.response.AlbumEnterResponse.AlbumHostInfo;
import com.cheeeese.album.dto.response.AlbumEnterResponse.AlbumParticipantResponse;
import com.cheeeese.album.infrastructure.persistence.UserAlbumRepository;
import com.cheeeese.user.domain.User;
import com.cheeeese.user.exception.UserException;
import com.cheeeese.user.exception.code.UserErrorCode;
import com.cheeeese.user.infrastructure.persistence.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AlbumService {

    private final AlbumValidator albumValidator;
    private final UserAlbumRepository userAlbumRepository;
    private final UserRepository userRepository;
    private final PhotoService photoService;

    public AlbumInvitationResponse getInvitationInfo(String code) {
        Album album = albumValidator.validateAlbumCode(code);

        albumValidator.validateAlbumExpiration(album);

        User host = userRepository.findById(album.getHostId())
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        return AlbumMapper.toInvitationResponse(album, host);
    }

    @Transactional
    public AlbumEnterResponse enterAlbum(String code, User currentUser) {
        // 1. 앨범 유효성 검증 (존재 여부)
        Album album = albumValidator.validateAlbumCode(code);

        // 2. 앨범 입장 인가 검증 (만료, 블랙리스트, 정원 초과)
        albumValidator.validateAlbumEntry(album, currentUser);

        // 3. 사용자 앨범 참가 로직: 첫 입장 시 GUEST로 등록 및 참가자 수 증가
        userAlbumRepository.findByUserIdAndAlbumId(currentUser.getId(), album.getId())
                .ifPresentOrElse(
                        userAlbum -> {
                            // 이미 참가한 경우: 아무것도 하지 않음
                        },
                        () -> {
                            // 첫 입장: UserAlbum에 GUEST로 저장하고, Album 참가자 수 증가
                            UserAlbum newUserAlbum = AlbumMapper.toGuestUserAlbum(currentUser, album);
                            userAlbumRepository.save(newUserAlbum);
                            album.incrementParticipantCount();
                        }
                );

        // 4. 응답 DTO 생성
        return createAlbumEnterResponse(album);
    }

    private AlbumEnterResponse createAlbumEnterResponse(Album album) {
        Long albumId = album.getId();

        // 1. 호스트 정보 조회
        User host = userRepository.findById(album.getHostId())
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        AlbumHostInfo hostInfo = AlbumMapper.toHostInfo(host);

        // 2. 총 사진 수
        // TODO: softdelete 논의 필요
        long totalPhotoCount = photoService.countTotalPhotos(albumId);

        // 3. 참가자 정보 목록 조회
        List<Long> participantUserIds = userAlbumRepository.findAllByAlbumId(albumId).stream()
                .map(UserAlbum::getUserId)
                .collect(Collectors.toList());
        List<User> participants = userRepository.findAllById(participantUserIds);

        List<AlbumParticipantResponse> participantResponses = participants.stream()
                .map(AlbumMapper::toParticipantResponse)
                .collect(Collectors.toList());

        // 4. 최신 사진 9장
        // TODO: 썸네일 생성 후 썸네일 이미지 제공, 아래 코드는 임시
        List<String> recentPhotoUrls = photoService.getRecentPhotoUrls(albumId);

        return AlbumMapper.toEnterResponse(
                album,
                hostInfo,
                totalPhotoCount,
                participantResponses,
                recentPhotoUrls
        );
    }
}
