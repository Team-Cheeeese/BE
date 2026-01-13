package com.cheeeese.album.application;

import com.cheeeese.album.domain.Album;
import com.cheeeese.album.exception.AlbumException;
import com.cheeeese.album.exception.code.AlbumErrorCode;
import com.cheeeese.album.infrastructure.persistence.AlbumRepository;
import com.cheeeese.cheese4cut.domain.Cheese4cut;
import com.cheeeese.cheese4cut.domain.Cheese4cutPhoto;
import com.cheeeese.cheese4cut.infrastructure.mapper.Cheese4cutMapper;
import com.cheeeese.cheese4cut.infrastructure.persistence.Cheese4cutRepository;
import com.cheeeese.photo.domain.Photo;
import com.cheeeese.photo.domain.PhotoStatus;
import com.cheeeese.photo.infrastructure.persistence.PhotoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlbumExpirationService {

    private static final int CHEESE4CUT_PHOTO_COUNT = 4;

    private final AlbumRepository albumRepository;
    private final PhotoRepository photoRepository;
    private final Cheese4cutRepository cheese4cutRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void expireAlbum(Long albumId) {
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new AlbumException(AlbumErrorCode.ALBUM_NOT_FOUND));

        if (album.getStatus() != Album.AlbumStatus.EXPIRED) {
            albumRepository.updateStatus(albumId, Album.AlbumStatus.EXPIRED);
            log.info("[AlbumExpiration] Album id={} status updated to EXPIRED", albumId);
        }

        List<Long> cheese4cutPhotoIds = cheese4cutRepository.findByAlbumId(albumId)
                .map(this::extractCheese4cutPhotoIds)
                .orElseGet(() -> createCheese4cutIfPossible(albumId, album));

        cleanupPhotosExceptCheese4cut(album, cheese4cutPhotoIds);
    }

    private List<Long> extractCheese4cutPhotoIds(Cheese4cut cheese4cut) {
        return cheese4cut.getPhotos().stream()
                .map(Cheese4cutPhoto::getPhotoId)
                .toList();
    }

    private List<Long> createCheese4cutIfPossible(Long albumId, Album album) {
        List<Long> topPhotoIds = photoRepository.findTop4CompletedPhotoIdsByLikes(
                albumId,
                PhotoStatus.COMPLETED,
                PageRequest.of(0, CHEESE4CUT_PHOTO_COUNT)
        );

        if (topPhotoIds.size() < CHEESE4CUT_PHOTO_COUNT) {
            log.warn("[AlbumExpiration] Album id={} has less than 4 photos, skipping cheese4cut creation", albumId);
            return List.of();
        }

        List<Photo> photos = photoRepository.findAllByIdIn(topPhotoIds);
        Map<Long, Photo> photoMap = photos.stream()
                .collect(Collectors.toMap(Photo::getId, Function.identity()));

        List<Photo> orderedPhotos = topPhotoIds.stream()
                .map(photoMap::get)
                .toList();

        if (orderedPhotos.stream().anyMatch(Objects::isNull)) {
            log.warn("[AlbumExpiration] Album id={} has missing photos for cheese4cut creation", albumId);
            return List.of();
        }

        cheese4cutRepository.save(Cheese4cutMapper.toEntity(album, orderedPhotos));

        log.info("[AlbumExpiration] Cheese4cut created automatically for album id={}", albumId);
        return topPhotoIds;
    }

    /**
     * 트랜잭션 안에서는 "DB 삭제"만 처리
     * 스토리지 삭제는 AFTER_COMMIT 이벤트로 넘김
     */
    private void cleanupPhotosExceptCheese4cut(Album album, List<Long> cheese4cutPhotoIds) {
        List<Photo> photosToDelete = cheese4cutPhotoIds.isEmpty()
                ? photoRepository.findAllByAlbumId(album.getId())
                : photoRepository.findAllByAlbumIdAndIdNotIn(
                        album.getId(),
                        cheese4cutPhotoIds
                );

        // 이벤트 payload 구성 (스토리지 삭제 대상 URL만 수집)
        List<AlbumStorageDeleteEvent.PhotoObjectDeleteTarget> photoObjectTargets = new ArrayList<>();
        for (Photo photo : photosToDelete) {
            photoObjectTargets.add(new AlbumStorageDeleteEvent.PhotoObjectDeleteTarget(
                    photo.getImageUrl(),
                    photo.getThumbnailUrl(),
                    true
            ));
        }

        // DB 삭제(트랜잭션 내)
        if (!photosToDelete.isEmpty()) {
            // photoRepository.deleteAll(photosToDelete);
            photoRepository.deleteAllInBatch(photosToDelete);

            log.info("[AlbumExpiration] Album id={} deleted photos count={}", album.getId(), photosToDelete.size());
        }

        // 트랜잭션 커밋 이후 실행될 이벤트 발행
        if (!photoObjectTargets.isEmpty()) {
            eventPublisher.publishEvent(new AlbumStorageDeleteEvent(
                    album.getId(),
                    photoObjectTargets
            ));
            log.info("[AlbumExpiration] Album id={} published storage delete event (photoObjects={})",
                    album.getId(), photoObjectTargets.size());
        }
    }
}
