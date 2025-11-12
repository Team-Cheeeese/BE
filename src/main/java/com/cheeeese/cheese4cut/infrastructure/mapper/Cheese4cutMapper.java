package com.cheeeese.cheese4cut.infrastructure.mapper;

import com.cheeeese.album.domain.Album;
import com.cheeeese.cheese4cut.domain.Cheese4cut;
import com.cheeeese.cheese4cut.domain.Cheese4cutPhoto;
import com.cheeeese.cheese4cut.dto.response.Cheese4cutFinalResponse;
import com.cheeeese.cheese4cut.dto.response.Cheese4cutPresignedUrlResponse;
import com.cheeeese.cheese4cut.dto.response.Cheese4cutPreviewResponse;
import com.cheeeese.photo.domain.Photo;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Cheese4cutMapper {

    private Cheese4cutMapper() {}

    /**
     * 확정 후 응답 (Cheese4cut 엔티티 기반)
     */
    public static Cheese4cutFinalResponse toFinalResponse(Cheese4cut cheese4cut) {
        return Cheese4cutFinalResponse.builder()
                .isFinalized(true)
                .photos(cheese4cut.getPhotos().stream()
                        .map(photo -> Cheese4cutFinalResponse.FinalPhotoInfo.builder()
                                .photoId(photo.getPhotoId())
                                .imageUrl(photo.getImageUrl())
                                .thumbnailImageUrl(photo.getThumbnailImageUrl())
                                .photoRank(photo.getPhotoRank())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }

    /**
     * 확정 전 응답 (좋아요 TOP 4 사진 목록 기반)
     */
    public static Cheese4cutPreviewResponse toPreviewResponse(
            List<Photo> topPhotos,
            long uniqueLikesCount,
            int participant
    ) {
        List<Cheese4cutPreviewResponse.PreviewPhotoInfo> photoInfos = topPhotos.stream()
                .map(photo -> Cheese4cutPreviewResponse.PreviewPhotoInfo.builder()
                        .photoId(photo.getId())
                        .imageUrl(photo.getImageUrl())
                        .build())
                .collect(Collectors.toList());

        return Cheese4cutPreviewResponse.builder()
                .isFinalized(false)
                .previewPhotos(photoInfos)
                .uniqueLikesCount((int) uniqueLikesCount)
                .participant(participant)
                .build();
    }

    /**
     * 확정 시 (좋아요 TOP4 또는 사용자가 선택한 사진 목록 기반)
     */
    public static Cheese4cut toEntity(Album album, List<Photo> orderedPhotos) {
        return Cheese4cut.builder()
                .album(album)
                .photos(IntStream.range(0, orderedPhotos.size())
                        .mapToObj(index -> {
                            Photo photo = orderedPhotos.get(index);
                            return Cheese4cutPhoto.builder()
                                    .photoId(photo.getId())
                                    .imageUrl(photo.getImageUrl())
                                    .thumbnailImageUrl(photo.getThumbnailUrl())
                                    .photoRank(index + 1)
                                    .build();
                        })
                        .collect(Collectors.toList()))
                .build();
    }

    public static Cheese4cutPresignedUrlResponse toPresignedUrlResponse(String uploadUrl) {
        return Cheese4cutPresignedUrlResponse.builder()
                .uploadUrl(uploadUrl).build();
    }
}
