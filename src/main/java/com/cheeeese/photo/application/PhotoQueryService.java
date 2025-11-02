package com.cheeeese.photo.application;

import com.cheeeese.global.util.RedisCacheUtil;
import com.cheeeese.photo.domain.Photo;
import com.cheeeese.photo.dto.response.PhotoPageResponse;
import com.cheeeese.photo.infrastructure.mapper.PhotoMapper;
import com.cheeeese.photo.infrastructure.persistence.PhotoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PhotoQueryService {

    private final PhotoRepository photoRepository;
    private final RedisCacheUtil redisCacheUtil;

    private static final String PHOTO_KEY = "album:%s:photos:page:%d:version:%d";
    private static final String VERSION_KEY = "album:%s:version";

    public PhotoPageResponse getPhotoPage(String code, int page, int size) {
        String versionKey = String.format(VERSION_KEY, code);
        Long curVersion = redisCacheUtil.getValue(versionKey);

        if (curVersion == null) {
            curVersion = 0L;
        }

        String photoKey = String.format(PHOTO_KEY, code, page, curVersion);
        PhotoPageResponse cachedList = redisCacheUtil.getObject(photoKey, PhotoPageResponse.class);

        // redis에 존재할 경우, db 접근 X + 바로 반환
        if (cachedList != null) {
            return cachedList;
        }
        PhotoPageResponse responses = getPhotoPageFromDB(code, page, size);

        redisCacheUtil.setValue(photoKey, responses, 300000L);

        return responses;
    }

    @Transactional
    public void invalidatePhotoCache(String code) {
        String versionKey = String.format(VERSION_KEY, code);
        Long curVersion = redisCacheUtil.getValue(versionKey);

        if (curVersion == null) {
            curVersion = 0L;
        }
        redisCacheUtil.setValue(versionKey, curVersion + 1, null);

        redisCacheUtil.deletePattern("album:" + code + ":photos:*");
    }

    private PhotoPageResponse getPhotoPageFromDB(String code, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Slice<Photo> photos = photoRepository.findAllByAlbumCode(code, pageRequest);
        return PhotoMapper.toPhotoPageResponse(photos);
    }
}
