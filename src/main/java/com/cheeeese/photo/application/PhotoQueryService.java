package com.cheeeese.photo.application;


import com.cheeeese.global.util.RedisCacheUtil;
import com.cheeeese.photo.dto.response.PhotoListResponse;
import com.cheeeese.photo.infrastructure.persistence.PhotoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PhotoQueryService {

    private final PhotoRepository photoRepository;
    private final RedisCacheUtil redisCacheUtil;

    private static final String PHOTO_KEY = "album:%s:photos:page:%d";
    private static final String VERSION_KEY = "album:%s:version";

    public List<PhotoListResponse> getPhotoList(
            String code,
            int page,
            int size
    ) {
        String photoKey = String.format(PHOTO_KEY, code, page);
        String versionKey = String.format(VERSION_KEY, code);

        Long curVersion = redisCacheUtil.getValue(versionKey);
        if (curVersion == null) {
            curVersion = 0L;
        }


    }
}
