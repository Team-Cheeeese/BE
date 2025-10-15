package com.cheeeese.album.infrastructure.mapper;

import com.cheeeese.album.domain.Album;
import com.cheeeese.album.domain.UserAlbum;
import com.cheeeese.album.domain.UserAlbumRole;
import com.cheeeese.album.dto.response.AlbumCreateResponse;
import com.cheeeese.album.dto.response.AlbumEnterResponse;
import com.cheeeese.album.dto.response.AlbumEnterResponse.AlbumHostInfo;
import com.cheeeese.album.dto.response.AlbumEnterResponse.AlbumParticipantResponse;
import com.cheeeese.album.dto.response.AlbumInvitationResponse;
import com.cheeeese.user.domain.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class AlbumMapper {

    /**
     * Album Entity 생성
     */
    public static Album toEntity(
            Long hostId,
            String title,
            String code,
            String themeImageUrl,
            int participant,
            LocalDate eventDate,
            boolean isInfoAvailable,
            LocalDateTime expiredAt
    ) {
        return Album.builder()
                .hostId(hostId)
                .title(title)
                .code(code)
                .themeImageUrl(themeImageUrl)
                .participant(participant)
                .currentParticipant(1)
                .eventDate(eventDate)
                .maxPhotoCount(2000)
                .currentPhotoCount(0)
                .isInfoAvailable(isInfoAvailable)
                .expiredAt(expiredAt)
                .status(Album.AlbumStatus.ACTIVE)
                .build();
    }

    /**
     * Album 생성 후, UUID Code 발급
     */
    public static AlbumCreateResponse toCreateResponse(Album album) {
        return AlbumCreateResponse.builder()
                .themeImageUrl(album.getThemeImageUrl())
                .eventDate(album.getEventDate())
                .currentParticipant(album.getCurrentParticipant())
                .code(album.getCode())
                .build();
    }

    /**
     * Album 엔티티와 Host User 정보를 초대장 응답 DTO로 변환합니다.
     */
    public static AlbumInvitationResponse toInvitationResponse(Album album, User host) {
        return AlbumInvitationResponse.builder()
                .title(album.getTitle())
                .themeImageUrl(album.getThemeImageUrl())
                .eventDate(album.getEventDate().toString())
                .expiredAt(album.getExpiredAt())
                .hostName(host.getName())
                .hostProfileImage(host.getProfileImage())
                .build();
    }

    /**
     * 앨범 입장 시 필요한 모든 정보들을 통합하여 응답 DTO로 변환합니다.
     */
    public static AlbumEnterResponse toEnterResponse(
            Album album,
            AlbumHostInfo hostInfo,
            long totalPhotoCount,
            List<AlbumParticipantResponse> participants,
            List<String> recentPhotoUrls
    ) {
        return AlbumEnterResponse.builder()
                .title(album.getTitle())
                .themeImageUrl(album.getThemeImageUrl())
                .eventDate(album.getEventDate().toString())
                .expiredAt(album.getExpiredAt())
                .maxParticipantCount(album.getParticipant())
                .currentParticipantCount(album.getCurrentParticipant())
                .hostInfo(hostInfo)
                .totalPhotoCount(totalPhotoCount)
                .maxPhotoCount(album.getMaxPhotoCount())
                .participants(participants)
                .recentPhotoUrls(recentPhotoUrls)
                .build();
    }

    /**
     * 호스트 User 엔티티를 호스트 정보 응답 DTO로 변환합니다.
     */
    public static AlbumHostInfo toHostInfo(User host) {
        return AlbumHostInfo.builder()
                .hostName(host.getName())
                .hostProfileImage(host.getProfileImage())
                .build();
    }

    /**
     * User와 Album 엔티티를 기반으로 GUEST 역할의 UserAlbum 엔티티를 생성합니다.
     */
    public static UserAlbum toGuestUserAlbum(User user, Album album) {
        return UserAlbum.builder()
                .userId(user.getId())
                .albumId(album.getId())
                .role(UserAlbumRole.GUEST)
                .build();
    }

    /**
     * 참가자 User 엔티티 리스트를 응답 DTO 리스트로 변환합니다.
     */
    public static AlbumParticipantResponse toParticipantResponse(User user) {
        return AlbumParticipantResponse.builder()
                .name(user.getName())
                .profileImage(user.getProfileImage())
                .build();
    }
}