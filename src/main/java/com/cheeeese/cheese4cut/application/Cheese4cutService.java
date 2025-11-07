package com.cheeeese.cheese4cut.application;

import com.cheeeese.album.application.validator.AlbumValidator;
import com.cheeeese.album.domain.Album;
import com.cheeeese.album.exception.AlbumException;
import com.cheeeese.album.exception.code.AlbumErrorCode;
import com.cheeeese.album.infrastructure.persistence.AlbumRepository;
import com.cheeeese.cheese4cut.application.validator.Cheese4cutValidator;
import com.cheeeese.cheese4cut.domain.Cheese4cut;
import com.cheeeese.cheese4cut.dto.request.Cheese4cutFixedRequest;
import com.cheeeese.cheese4cut.dto.response.Cheese4cutPresignedUrlResponse;
import com.cheeeese.cheese4cut.dto.response.Cheese4cutResponse;
import com.cheeeese.cheese4cut.exception.Cheese4cutException;
import com.cheeeese.cheese4cut.exception.code.Cheese4cutErrorCode;
import com.cheeeese.cheese4cut.infrastructure.mapper.Cheese4cutMapper;
import com.cheeeese.cheese4cut.infrastructure.persistence.Cheese4cutRepository;
import com.cheeeese.photo.application.PresignedUrlService;
import com.cheeeese.photo.domain.Photo;
import com.cheeeese.photo.infrastructure.persistence.PhotoLikesRepository;
import com.cheeeese.photo.infrastructure.persistence.PhotoRepository;
import com.cheeeese.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class Cheese4cutService {

    private final Cheese4cutRepository cheese4cutRepository;
    private final AlbumRepository albumRepository;
    private final PhotoRepository photoRepository;
    private final PhotoLikesRepository photoLikesRepository;
    private final AlbumValidator albumValidator;
    private final PresignedUrlService presignedUrlService;
    private final Cheese4cutValidator cheese4cutValidator;

    @Transactional(readOnly = true)
    public Cheese4cutResponse getCheese4cutByAlbumCode(String code) {
        Album album = albumRepository.findByCode(code)
                .orElseThrow(() -> new AlbumException(AlbumErrorCode.ALBUM_NOT_FOUND));

        Optional<Cheese4cut> cheese4cutOptional = cheese4cutRepository.findByAlbumId(album.getId());

        if (cheese4cutOptional.isPresent()) {
            return Cheese4cutMapper.toFinalResponse(cheese4cutOptional.get());
        }

        return getPreviewResponse(album.getId(), album.getParticipant());
    }

    private Cheese4cutResponse getPreviewResponse(Long albumId, int participant) {
        List<Long> topPhotoIds = photoRepository.findTop4CompletedPhotoIdsByLikes(
                albumId,
                PageRequest.of(0, 4)
        );

        if (topPhotoIds.size() < 4) {
            throw new Cheese4cutException(Cheese4cutErrorCode.INSUFFICIENT_COUNT_FOR_CHEESE4CUT);
        }

        List<Photo> topPhotos = photoRepository.findAllById(topPhotoIds);

        Map<Long, Photo> photoMap = topPhotos.stream()
                .collect(Collectors.toMap(Photo::getId, Function.identity()));

        List<Photo> orderedPhotos = topPhotoIds.stream()
                .map(photoMap::get)
                .toList();

        long uniqueLikesCount = photoLikesRepository.countDistinctUserIdsByPhotoIds(topPhotoIds);

        return Cheese4cutMapper.toPreviewResponse(orderedPhotos, uniqueLikesCount, participant);
    }

    @Transactional(readOnly = true)
    public Cheese4cutPresignedUrlResponse createCheese4cutPresignedUrl(User user, String code) {
        Album album = albumValidator.validateAlbumCode(code);
        albumValidator.validateUploadPermission(album, user);

        String uploadUrl = presignedUrlService.generateCheese4cutPresignedPutUrl(code);

        return Cheese4cutMapper.toPresignedUrlResponse(uploadUrl);
    }

    public void finalizeCheese4cut(User user, String code, Cheese4cutFixedRequest request) {
        Album album = albumValidator.validateAlbumCode(code);

        if (album.isExpired()) {
            throw new AlbumException(AlbumErrorCode.ALBUM_EXPIRED);
        }

        cheese4cutValidator.validateUserIsMaker(album, user);

        if (cheese4cutRepository.findByAlbumId(album.getId()).isPresent()) {
            throw new Cheese4cutException(Cheese4cutErrorCode.CHEESE4CUT_ALREADY_FINALIZED);
        }

        cheese4cutValidator.validateFinalizePhotos(album, request.photoIds());

        Cheese4cut cheese4cut = Cheese4cutMapper.toEntity(album, request);

        cheese4cutRepository.save(cheese4cut);
    }
}
