package com.cheeeese.album.infrastructure.mapper;

import com.cheeeese.album.domain.Album;
import com.cheeeese.album.dto.response.AlbumCreationResponse;
import com.cheeeese.album.dto.response.AlbumEnterResponse;
import com.cheeeese.album.dto.response.AlbumEnterResponse.AlbumHostInfo;
import com.cheeeese.album.dto.response.AlbumEnterResponse.AlbumParticipantResponse;
import com.cheeeese.album.dto.response.AlbumInvitationResponse;
import com.cheeeese.album.dto.response.UploadAvailableCountResponse;
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
            String themeEmoji,
            int participant,
            LocalDate eventDate,
            boolean isInfoAvailable,
            LocalDateTime expiredAt,
            boolean isTermsAgreement
    ) {
        return Album.builder()
                .hostId(hostId)
                .title(title)
                .code(code)
                .themeEmoji(themeEmoji)
                .participant(participant)
                .currentParticipant(1)
                .eventDate(eventDate)
                .maxPhotoCount(2000)
                .currentPhotoCount(0)
                .isInfoAvailable(isInfoAvailable)
                .expiredAt(expiredAt)
                .status(Album.AlbumStatus.ACTIVE)
                .isTermsAgreement(isTermsAgreement)
                .build();
    }

    /**
     * Album 생성 후, UUID Code 발급
     */
    public static AlbumCreationResponse toCreationResponse(Album album) {
        return AlbumCreationResponse.builder()
                .themeEmoji(album.getThemeEmoji())
                .title(album.getTitle())
                .eventDate(album.getEventDate())
                .currentPhotoCnt(album.getCurrentPhotoCount())
                .code(album.getCode())
                .build();
    }

    /**
     * Album 엔티티와 Host User 정보를 초대장 응답 DTO로 변환합니다.
     */
    public static AlbumInvitationResponse toInvitationResponse(Album album, User host) {
        return AlbumInvitationResponse.builder()
                .title(album.getTitle())
                .themeEmoji(album.getThemeEmoji())
                .eventDate(album.getEventDate().toString())
                .expiredAt(album.getExpiredAt())
                .hostName(host.getName())
                .hostProfileImage(host.getProfileImage())
                .isExpired(false)
                .build();
    }

    /**
     * 앨범 만료 시, 최소 정보만 담아 응답 DTO로 변환합니다.
     */
    public static AlbumInvitationResponse toExpiredInvitationResponse(Album album) {
        return AlbumInvitationResponse.builder()
                .title(album.getTitle())
                .themeEmoji(album.getThemeEmoji())
                .eventDate(album.getEventDate().toString())
                .expiredAt(album.getExpiredAt())
                .hostName(null)
                .hostProfileImage(null)
                .isExpired(true)
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
                .themeEmoji(album.getThemeEmoji())
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
     * 참가자 User 엔티티 리스트를 응답 DTO 리스트로 변환합니다.
     */
    public static AlbumParticipantResponse toParticipantResponse(User user) {
        return AlbumParticipantResponse.builder()
                .name(user.getName())
                .profileImage(user.getProfileImage())
                .build();
    }

    public static UploadAvailableCountResponse toAvailableCountResponse(
            int availableCount,
            int maxCount,
            int currentCount
    ){
        return UploadAvailableCountResponse.builder()
                .availableCount(availableCount)
                .maxPhotoCount(maxCount)
                .currentPhotoCount(currentCount)
                .build();
    }
}