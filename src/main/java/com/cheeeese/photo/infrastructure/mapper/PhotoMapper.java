package com.cheeeese.photo.infrastructure.mapper;

import com.cheeeese.album.domain.Album;
import com.cheeeese.album.domain.type.Role;
import com.cheeeese.photo.domain.Photo;
import com.cheeeese.photo.domain.PhotoStatus;
import com.cheeeese.photo.dto.response.*;
import com.cheeeese.user.domain.User;
import org.springframework.data.domain.Slice;

import java.time.LocalDateTime;
import java.util.List;

public class PhotoMapper {

    public static Photo toEntity(User user, Album album, LocalDateTime captureTime) {
        return Photo.builder()
                .user(user)
                .album(album)
                .imageUrl(null) // presigned URL 생성 후 updateImageUrl()로 세팅됨
                .thumbnailUrl(null)
                .captureTime(captureTime != null ? captureTime : LocalDateTime.now())
                .status(PhotoStatus.UPLOADING)
                .build();
    }

    public static PhotoPresignedUrlResponse.PresignedUrlInfo toPresignedUrlInfo(
            Long photoId,
            String uploadUrl
    ) {
        return PhotoPresignedUrlResponse.PresignedUrlInfo.builder()
                .photoId(photoId)
                .uploadUrl(uploadUrl)
                .build();
    }

    public static PhotoPresignedUrlResponse toPresignedUrlResponse(
            List<PhotoPresignedUrlResponse.PresignedUrlInfo> presignedUrlInfos
    ) {
        return PhotoPresignedUrlResponse.builder()
                .presignedUrlInfos(presignedUrlInfos)
                .build();
    }

    public static PhotoDownloadResponse.DownloadFileInfo toDownloadPresignedUrlInfo(
            Photo photo,
            String fileName,
            String downloadUrl
    ) {
        return PhotoDownloadResponse.DownloadFileInfo.builder()
                .photoId(photo.getId())
                .downloadUrl(downloadUrl)
                .fileName(fileName)
                .captureTime(photo.getCaptureTime())
                .createdAt(photo.getCreatedAt())
                .build();
    }

    public static PhotoDownloadResponse toPhotoDownloadResponse(List<PhotoDownloadResponse.DownloadFileInfo> downloadFileInfos) {
        return PhotoDownloadResponse.builder()
                .downloadFiles(downloadFileInfos)
                .build();
    }

    public static PhotoListResponse toPhotoListResponse(
            Photo photo,
            String profileImage,
            String imageUrl,
            String thumbnailUrl,
            boolean isLiked,
            boolean isDownloaded
    ) {
        return PhotoListResponse.builder()
                .name(photo.getUser().getName())
                .photoId(photo.getId())
                .profileImage(profileImage)
                .imageUrl(imageUrl)
                .thumbnailUrl(thumbnailUrl)
                .likeCnt(photo.getLikesCnt())
                .isLiked(isLiked)
                .isDownloaded(isDownloaded)
                .isRecentlyDownloaded(false)
                .canDelete(false)
                .build();
    }

    public static PhotoLikedResponse toPhotoLikedResponse(
            Photo photo,
            String imageUrl,
            String thumbnailUrl,
            boolean isLiked,
            boolean isDownloaded,
            boolean isRecentlyDownloaded
    ) {
        return PhotoLikedResponse.builder()
                .name(photo.getUser().getName())
                .photoId(photo.getId())
                .imageUrl(imageUrl)
                .thumbnailUrl(thumbnailUrl)
                .likeCnt(photo.getLikesCnt())
                .isLiked(isLiked)
                .isDownloaded(isDownloaded)
                .isRecentlyDownloaded(isRecentlyDownloaded)
                .build();
    }

    public static PhotoPageResponse toPhotoPageResponse(Slice<Photo> photos, List<PhotoListResponse> responses) {
        return PhotoPageResponse.builder()
                .responses(responses)
                .listSize(responses.size())
                .isFirst(photos.isFirst())
                .isLast(photos.isLast())
                .hasNext(photos.hasNext())
                .build();
    }

    public static PhotoLikedPageResponse toPhotoLikedPageResponse(Slice<Photo> photos, List<PhotoLikedResponse> responses) {
        return PhotoLikedPageResponse.builder()
                .responses(responses)
                .listSize(responses.size())
                .isFirst(photos.isFirst())
                .isLast(photos.isLast())
                .hasNext(photos.hasNext())
                .build();
    }

    public static PhotoPageResponse toRebuildPhotoPageResponse(PhotoPageResponse response, List<PhotoListResponse> updated) {
        return PhotoPageResponse.builder()
                .responses(updated)
                .listSize(updated.size())
                .isFirst(response.isFirst())
                .isLast(response.isLast())
                .hasNext(response.hasNext())
                .build();
    }

    public static PhotoDetailResponse toPhotoDetailResponse(
            Photo photo,
            String profileImage,
            String imageUrl,
            String thumbnailUrl,
            boolean isLiked,
            boolean isDownloaded,
            boolean isRecentlyDownloaded,
            boolean canDelete
    ) {
        return PhotoDetailResponse.builder()
                .name(photo.getUser().getName())
                .photoId(photo.getId())
                .profileImage(profileImage)
                .imageUrl(imageUrl)
                .thumbnailUrl(thumbnailUrl)
                .likesCnt(photo.getLikesCnt())
                .isLiked(isLiked)
                .isDownloaded(isDownloaded)
                .isRecentlyDownloaded(isRecentlyDownloaded)
                .canDelete(canDelete)
                .captureTime(photo.getCaptureTime())
                .createdAt(photo.getCreatedAt())
                .build();
    }

    public static PhotoLikedUserResponse.PhotoLiker toPhotoLiker(User user, String profileImageUrl, boolean isMe, Role role) {
        return PhotoLikedUserResponse.PhotoLiker.builder()
                .name(user.getName())
                .profileImageUrl(profileImageUrl)
                .isMe(isMe)
                .role(role)
                .build();
    }

    public static PhotoLikedUserResponse toPhotoLikerResponse(
            Photo photo,
            List<PhotoLikedUserResponse.PhotoLiker> likers
    ) {
        return PhotoLikedUserResponse.builder()
                .likeCnt(photo.getLikesCnt())
                .photoLikers(likers)
                .build();
    }
}
