package com.cheeeese.cheese4cut.infrastructure.mapper;

import com.cheeeese.album.domain.Album;
import com.cheeeese.album.domain.type.Role;
import com.cheeeese.cheese4cut.domain.AiSummaryStatus;
import com.cheeeese.cheese4cut.domain.Cheese4cut;
import com.cheeeese.cheese4cut.domain.Cheese4cutAiSummary;
import com.cheeeese.cheese4cut.domain.Cheese4cutPhoto;
import com.cheeeese.cheese4cut.dto.response.Cheese4cutAiResponse;
import com.cheeeese.cheese4cut.dto.response.Cheese4cutFinalResponse;
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
    public static Cheese4cutFinalResponse toFinalResponse(List<Cheese4cutFinalResponse.FinalPhotoInfo> photos) {
        return Cheese4cutFinalResponse.builder()
                .isFinalized(true)
                .photos(photos)
                .build();
    }

    /**
     * 확정 전 응답 (좋아요 TOP 4 사진 목록 기반)
     */
    public static Cheese4cutPreviewResponse toPreviewResponse(
            List<Cheese4cutPreviewResponse.PreviewPhotoInfo> photoInfos,
            long uniqueLikesCount,
            int participant,
            Role myRole
    ) {
        return Cheese4cutPreviewResponse.builder()
                .isFinalized(false)
                .previewPhotos(photoInfos)
                .uniqueLikesCount((int) uniqueLikesCount)
                .participant(participant)
                .myRole(myRole)
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

    public static Cheese4cutFinalResponse.FinalPhotoInfo toFinalPhotoInfo(
            Long photoId, String imageUrl, int rank) {

        return Cheese4cutFinalResponse.FinalPhotoInfo.builder()
                .photoId(photoId)
                .imageUrl(imageUrl)
                .photoRank(rank)
                .build();
    }

    public static Cheese4cutPreviewResponse.PreviewPhotoInfo toPreviewPhotoInfo(
            Long photoId, String imageUrl, int rank) {
        return Cheese4cutPreviewResponse.PreviewPhotoInfo.builder()
                .photoId(photoId)
                .imageUrl(imageUrl)
                .photoRank(rank)
                .build();
    }

    // AI 생성 시작 시 (PROCESSING 상태)
    public static Cheese4cutAiSummary toAiSummaryProcessing(Cheese4cut cheese4cut) {
        return Cheese4cutAiSummary.builder()
                .cheese4cut(cheese4cut)
                .status(AiSummaryStatus.PROCESSING)
                .build();
    }

    // AI 요약 결과 DTO 변환
    public static Cheese4cutAiResponse toAiResponse(Cheese4cutAiSummary summary) {
        return switch (summary.getStatus()) {
            case COMPLETED -> Cheese4cutAiResponse.completed(summary.getAiTitle(), summary.getAiContent());
            case FAILED -> Cheese4cutAiResponse.failed();
            default -> Cheese4cutAiResponse.processing();
        };
    }

}
