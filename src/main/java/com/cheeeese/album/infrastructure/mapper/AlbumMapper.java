package com.cheeeese.album.infrastructure.mapper;

import com.cheeeese.album.domain.Album;
import com.cheeeese.album.domain.type.AlbumJoinStatus;
import com.cheeeese.album.dto.response.*;
import com.cheeeese.photo.domain.Photo;
import com.cheeeese.album.dto.response.AlbumBest4CutResponse;
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
            LocalDateTime expiredAt
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
                .build();
    }

    /**
     * Album 생성 후, UUID Code 발급
     */
    public static AlbumCreationResponse toCreationResponse(Album album, boolean isFirst) {
        return AlbumCreationResponse.builder()
                .themeEmoji(album.getThemeEmoji())
                .title(album.getTitle())
                .eventDate(album.getEventDate())
                .createdAt(album.getCreatedAt())
                .isFirst(isFirst)
                .currentPhotoCnt(album.getCurrentPhotoCount())
                .code(album.getCode())
                .build();
    }

    /**
     * Album 엔티티와 Maker User 정보를 초대장 응답 DTO로 변환합니다.
     */
    public static AlbumInvitationResponse toInvitationResponse(Album album, User user, String profileImage) {
        return AlbumInvitationResponse.builder()
                .title(album.getTitle())
                .themeEmoji(album.getThemeEmoji())
                .eventDate(album.getEventDate().toString())
                .expiredAt(album.getExpiredAt())
                .makerName(user.getName())
                .makerProfileImage(profileImage)
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
                .makerName(null)
                .makerProfileImage(null)
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
            List<NewEnterResponse.RecentPhotoResponse> recentPhotos
    ) {
        return NewEnterResponse.builder()
                .joinStatus(AlbumJoinStatus.NEW)
                .title(album.getTitle())
                .themeEmoji(album.getThemeEmoji())
                .eventDate(album.getEventDate().toString())
                .expiredAt(album.getExpiredAt())
                .makerInfo(makerInfo)
                .remainingUploadSlots(remainingUploadSlots)
                .recentPhotos(recentPhotos)
                .build();
    }

    public static NewEnterResponse.RecentPhotoResponse toRecentPhotoResponse(Photo photo, String profileImage) {
        User uploader = photo.getUser();

        return NewEnterResponse.RecentPhotoResponse.builder()
                .thumbnailUrl(photo.getThumbnailUrl())
                .uploaderName(uploader.getName())
                .uploaderProfileImage(profileImage)
                .build();
    }

    /**
     * 메이커 User 엔티티를 호스트 정보 응답 DTO로 변환합니다.
     */
    public static AlbumMakerInfo toMakerInfo(User user, String profileImage) {
        return AlbumMakerInfo.builder()
                .makerName(user.getName())
                .makerProfileImage(profileImage)
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

    public static AlbumBest4CutResponse toBest4CutResponse(Photo photo, String thumbnailUrl, boolean isLiked) {
        return AlbumBest4CutResponse.builder()
                .thumbnailUrl(thumbnailUrl)
                .likeCnt(photo.getLikesCnt())
                .isLiked(isLiked)
                .build();
    }

    public static AlbumInfoResponse toAlbumInfoResponse(Album album, User user) {
        return AlbumInfoResponse.builder()
                .title(album.getTitle())
                .makerId(album.getMakerId())
                .name(user.getName())
                .themeEmoji(album.getThemeEmoji())
                .participant(album.getParticipant())
                .currentParticipant(album.getCurrentParticipant())
                .eventDate(album.getEventDate())
                .currentPhotoCnt(album.getCurrentPhotoCount())
                .expiredAt(album.getExpiredAt())
                .build();
    }
}