package com.cheeeese.photo.infrastructure.mapper;

import com.cheeeese.album.domain.Album;
import com.cheeeese.photo.domain.Photo;
import com.cheeeese.photo.domain.PhotoStatus;
import com.cheeeese.photo.dto.response.PhotoDetailResponse;
import com.cheeeese.photo.dto.response.PhotoListResponse;
import com.cheeeese.photo.dto.response.PhotoPageResponse;
import com.cheeeese.photo.dto.response.PhotoPresignedUrlResponse;
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

    public static PhotoListResponse toPhotoListResponse(Photo photo, boolean isLiked, boolean isDownloaded) {
        return PhotoListResponse.builder()
                .photoId(photo.getId())
                .thumbnailUrl(photo.getThumbnailUrl())
                .likeCnt(photo.getLikesCnt())
                .isLiked(isLiked)
                .isDownloaded(isDownloaded)
                .build();
    }

    public static PhotoPageResponse toPhotoPageResponse(Slice<Photo> photos) {
        List<PhotoListResponse> responses = photos.getContent().stream()
                .map(photo -> PhotoMapper.toPhotoListResponse(photo, false, false))
                .toList();

        return PhotoPageResponse.builder()
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

    public static PhotoDetailResponse toPhotoDetailResponse(Photo photo, boolean isLiked, boolean isDownloaded) {
        return PhotoDetailResponse.builder()
                .photoId(photo.getId())
                .imageUrl(photo.getImageUrl())
                .thumbnailUrl(photo.getThumbnailUrl())
                .likesCnt(photo.getLikesCnt())
                .isLiked(isLiked)
                .isDownloaded(isDownloaded)
                .build();
    }
}
