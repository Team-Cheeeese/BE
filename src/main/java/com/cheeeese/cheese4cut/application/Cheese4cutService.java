package com.cheeeese.cheese4cut.application;

import com.cheeeese.album.domain.Album;
import com.cheeeese.album.exception.AlbumException;
import com.cheeeese.album.exception.code.AlbumErrorCode;
import com.cheeeese.album.infrastructure.persistence.AlbumRepository;
import com.cheeeese.cheese4cut.domain.Cheese4cut;
import com.cheeeese.cheese4cut.dto.response.Cheese4cutResponse;
import com.cheeeese.cheese4cut.infrastructure.mapper.Cheese4cutMapper;
import com.cheeeese.cheese4cut.infrastructure.persistence.Cheese4cutRepository;
import com.cheeeese.photo.domain.Photo;
import com.cheeeese.photo.infrastructure.persistence.PhotoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class Cheese4cutService {

    private final Cheese4cutRepository cheese4cutRepository;
    private final AlbumRepository albumRepository;
    private final PhotoRepository photoRepository;

    @Transactional(readOnly = true)
    public Cheese4cutResponse getCheese4cutByAlbumCode(String code) {
        Album album = albumRepository.findByCode(code)
                .orElseThrow(() -> new AlbumException(AlbumErrorCode.ALBUM_NOT_FOUND));

        Optional<Cheese4cut> cheese4cutOptional = cheese4cutRepository.findByAlbumId(album.getId());

        if (cheese4cutOptional.isPresent()) {
            return Cheese4cutMapper.toFinalResponse(cheese4cutOptional.get());
        }

        return getPreviewResponse(album.getId());
    }

    private Cheese4cutResponse getPreviewResponse(Long albumId) {
        List<Long> topPhotoIds = photoRepository.findTop4CompletedPhotoIdsByLikes(
                albumId,
                PageRequest.of(0, 4)
        );

        List<Photo> topPhotos = photoRepository.findAllById(topPhotoIds);

        return Cheese4cutMapper.toPreviewResponse(topPhotos);
    }
}
