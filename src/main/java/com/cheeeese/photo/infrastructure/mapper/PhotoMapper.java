package com.cheeeese.photo.infrastructure.mapper;

import com.cheeeese.album.domain.Album;
import com.cheeeese.global.util.resolver.CdnUrlResolver;
import com.cheeeese.photo.domain.Photo;
import com.cheeeese.photo.domain.PhotoStatus;
import com.cheeeese.photo.dto.response.*;
import com.cheeeese.user.domain.User;
import org.springframework.data.domain.Slice;

import java.time.LocalDateTime;
import java.util.List;

public class PhotoMapper {

    public static Photo toEntity(User user, Album album) {
        return Photo.builder()
                .user(user)
                .album(album)
                .imageUrl(null) // presigned URL 생성 후 updateImageUrl()로 세팅됨
                .thumbnailUrl(null)
                .captureTime(LocalDateTime.now())
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

    public static PhotoDownloadResponse.DownloadFileInfo toDownloadPresignedUrlInfo(Photo photo, String downloadUrl) {
        return PhotoDownloadResponse.DownloadFileInfo.builder()
                .photoId(photo.getId())
                .downloadUrl(downloadUrl)
                .fileName(photo.getImageUrl())
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
            String thumbnailUrl,
            boolean isLiked,
            boolean isDownloaded
    ) {
        return PhotoListResponse.builder()
                .photoId(photo.getId())
                .thumbnailUrl(thumbnailUrl)
                .likeCnt(photo.getLikesCnt())
                .isLiked(isLiked)
                .isDownloaded(isDownloaded)
                .build();
    }

    public static PhotoLikedResponse toPhotoLikedResponse(
            Photo photo,
            String thumbnailUrl,
            boolean isDownloaded,
            boolean isRecentlyDownloaded
    ) {
        return PhotoLikedResponse.builder()
                .photoId(photo.getId())
                .thumbnailUrl(thumbnailUrl)
                .isDownloaded(isDownloaded)
                .isRecentlyDownloaded(isRecentlyDownloaded)
                .build();
    }

    public static PhotoPageResponse toPhotoPageResponse(Slice<Photo> photos, CdnUrlResolver cdnUrlResolver) {
        List<PhotoListResponse> responses = photos.getContent().stream()
                .map(photo -> {
                    String resolvedUrl = cdnUrlResolver.resolveThumbnail(photo.getThumbnailUrl());
                    return PhotoMapper.toPhotoListResponse(photo, resolvedUrl, false, false);
                })
                .toList();

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

    public static PhotoDetailResponse toPhotoDetailResponse(Photo photo, boolean isLiked, boolean isDownloaded, boolean isRecentlyDownloaded) {
        return PhotoDetailResponse.builder()
                .name(photo.getUser().getName())
                .photoId(photo.getId())
                .imageUrl(photo.getImageUrl())
                .thumbnailUrl(photo.getThumbnailUrl())
                .likesCnt(photo.getLikesCnt())
                .isLiked(isLiked)
                .isDownloaded(isDownloaded)
                .isRecentlyDownloaded(isRecentlyDownloaded)
                .build();
    }
}
