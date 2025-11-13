package com.cheeeese.album.application;

import com.cheeeese.album.domain.Album;
import com.cheeeese.album.exception.AlbumException;
import com.cheeeese.album.exception.code.AlbumErrorCode;
import com.cheeeese.album.infrastructure.persistence.AlbumRepository;
import com.cheeeese.cheese4cut.infrastructure.mapper.Cheese4cutMapper;
import com.cheeeese.cheese4cut.infrastructure.persistence.Cheese4cutRepository;
import com.cheeeese.photo.domain.Photo;
import com.cheeeese.photo.domain.PhotoStatus;
import com.cheeeese.photo.infrastructure.persistence.PhotoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public void expireAlbum(Long albumId) {
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new AlbumException(AlbumErrorCode.ALBUM_NOT_FOUND));

        if (album.getStatus() != Album.AlbumStatus.EXPIRED) {
            albumRepository.updateStatus(albumId, Album.AlbumStatus.EXPIRED);
            log.info("[AlbumExpiration] Album id={} status updated to EXPIRED", albumId);
        }

        if (cheese4cutRepository.findByAlbumId(albumId).isPresent()) {
            return;
        }

        List<Long> topPhotoIds = photoRepository.findTop4CompletedPhotoIdsByLikes(
                albumId,
                PhotoStatus.COMPLETED,
                PageRequest.of(0, CHEESE4CUT_PHOTO_COUNT)
        );

        if (topPhotoIds.size() < CHEESE4CUT_PHOTO_COUNT) {
            log.warn(
                    "[AlbumExpiration] Album id={} does not have enough photos to create cheese4cut (found={})",
                    albumId,
                    topPhotoIds.size()
            );
            return;
        }

        List<Photo> photos = photoRepository.findAllByIdIn(topPhotoIds);
        Map<Long, Photo> photoMap = photos.stream()
                .collect(Collectors.toMap(Photo::getId, Function.identity()));

        List<Photo> orderedPhotos = topPhotoIds.stream()
                .map(photoMap::get)
                .collect(Collectors.toList());

        if (orderedPhotos.stream().anyMatch(Objects::isNull)) {
            log.warn("[AlbumExpiration] Album id={} has missing photos for cheese4cut creation", albumId);
            return;
        }

        cheese4cutRepository.save(Cheese4cutMapper.toEntity(album, orderedPhotos));

        log.info("[AlbumExpiration] Cheese4cut created automatically for album id={}", albumId);
    }
}