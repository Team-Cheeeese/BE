package com.cheeeese.cheese4cut.infrastructure.mapper;

import com.cheeeese.cheese4cut.domain.Cheese4cut;
import com.cheeeese.cheese4cut.dto.response.Cheese4cutFinalResponse;
import com.cheeeese.cheese4cut.dto.response.Cheese4cutPreviewResponse;
import com.cheeeese.photo.domain.Photo;

import java.util.List;
import java.util.stream.Collectors;

public class Cheese4cutMapper {

    private Cheese4cutMapper() {}

    /**
     * 확정 후 응답 (Cheese4cut 엔티티 기반)
     */
    public static Cheese4cutFinalResponse toFinalResponse(Cheese4cut cheese4cut) {
        return Cheese4cutFinalResponse.builder()
                .isFinalized(true)
                .finalFrameImageUrl(cheese4cut.getFrameImageUrl())
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
}
