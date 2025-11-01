package com.cheeeese.photo.infrastructure.mapper;

import com.cheeeese.album.domain.Album;
import com.cheeeese.photo.domain.Photo;
import com.cheeeese.photo.domain.PhotoStatus;
import com.cheeeese.photo.dto.response.PhotoPresignedUrlResponse;
import com.cheeeese.user.domain.User;

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
}
