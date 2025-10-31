package com.cheeeese.album.infrastructure.mapper;

import com.cheeeese.album.domain.Album;
import com.cheeeese.album.domain.type.AlbumJoinStatus;
import com.cheeeese.album.dto.response.*;
import com.cheeeese.user.domain.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class AlbumMapper {

    /**
     * Album Entity 생성
     */
    public static Album toEntity(
            Long makerId,
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
                .makerId(makerId)
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
     * Album 엔티티와 Maker User 정보를 초대장 응답 DTO로 변환합니다.
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

    public static ExistingEnterResponse toExistingResponse(Album album, AlbumJoinStatus status, AlbumMakerInfo makerInfo) {
        return ExistingEnterResponse.builder()
                .joinStatus(status)
                .title(album.getTitle())
                .themeEmoji(album.getThemeEmoji())
                .eventDate(album.getEventDate().toString())
                .expiredAt(album.getExpiredAt())
                .makerInfo(makerInfo)
                .build();
    }

    public static NewEnterResponse toNewResponse(
            Album album,
            AlbumMakerInfo makerInfo,
            int remainingUploadSlots,
            List<String> recentPhotoUrls
    ) {
        return NewEnterResponse.builder()
                .joinStatus(AlbumJoinStatus.NEW)
                .title(album.getTitle())
                .themeEmoji(album.getThemeEmoji())
                .eventDate(album.getEventDate().toString())
                .expiredAt(album.getExpiredAt())
                .makerInfo(makerInfo)
                .remainingUploadSlots(remainingUploadSlots)
                .recentPhotoUrls(recentPhotoUrls)
                .build();
    }

    /**
     * 메이커 User 엔티티를 호스트 정보 응답 DTO로 변환합니다.
     */
    public static AlbumMakerInfo toMakerInfo(User user) {
        return AlbumMakerInfo.builder()
                .makerName(user.getName())
                .makerProfileImage(user.getProfileImage())
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