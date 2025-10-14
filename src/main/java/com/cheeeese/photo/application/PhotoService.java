package com.cheeeese.photo.application;

import com.cheeeese.photo.domain.Photo;
import com.cheeeese.photo.infrastructure.persistence.PhotoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PhotoService {

    private final PhotoRepository photoRepository;

    public long countTotalPhotos(Long albumId) {
        return photoRepository.countByAlbumIdAndIsDeletedFalse(albumId);
    }

    public List<String> getRecentPhotoUrls(Long albumId) {
        List<Photo> recentPhotos = photoRepository.findTop9ByAlbumIdAndIsDeletedFalseOrderByCreatedAtDesc(albumId);
        return recentPhotos.stream()
                .map(Photo::getImageUrl)
                .collect(Collectors.toList());
    }
}
