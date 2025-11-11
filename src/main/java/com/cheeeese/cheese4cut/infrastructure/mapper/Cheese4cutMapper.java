package com.cheeeese.cheese4cut.infrastructure.mapper;

import com.cheeeese.album.domain.Album;
import com.cheeeese.cheese4cut.domain.Cheese4cut;
import com.cheeeese.cheese4cut.dto.request.Cheese4cutFixedRequest;
import com.cheeeese.cheese4cut.dto.response.Cheese4cutFinalResponse;
import com.cheeeese.cheese4cut.dto.response.Cheese4cutPresignedUrlResponse;
import com.cheeeese.cheese4cut.dto.response.Cheese4cutPreviewResponse;
import com.cheeeese.photo.domain.Photo;

import java.util.List;
import java.util.stream.Collectors;

public class Cheese4cutMapper {

    private Cheese4cutMapper() {}

    /**
     * 확정 후 응답 (Cheese4cut 엔티티 기반)
     */
    // TODO: 프레임 이미지 제거에 따른 응답 형식 수정하기
    public static Cheese4cutFinalResponse toFinalResponse() {
        return Cheese4cutFinalResponse.builder()
                .isFinalized(true)
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
     * 사용자가 직접 확정할 때 요청 DTO 기반 엔티티 변환
     */
    public static Cheese4cut toEntity(Album album, Cheese4cutFixedRequest request) {
        return Cheese4cut.builder()
                .album(album)
                .photoIds(request.photoIds())
                .build();
    }

    /**
     * 만료 자동 확정 시 (top4 사진 및 기본 프레임 기반)
     */
    public static Cheese4cut toEntity(Album album, List<Long> photoIds) {
        return Cheese4cut.builder()
                .album(album)
                .photoIds(photoIds)
                .build();
    }

    public static Cheese4cutPresignedUrlResponse toPresignedUrlResponse(String uploadUrl) {
        return Cheese4cutPresignedUrlResponse.builder()
                .uploadUrl(uploadUrl).build();
    }
}
