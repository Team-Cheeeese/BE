package com.cheeeese.photo.application;

import com.cheeeese.global.util.RedisCacheUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PhotoCacheInvalidator {

    private static final String VERSION_KEY = "cache:album:%s:version";
    private static final String PHOTO_KEY_PATTERN = "cache:album:%s:photos:*";

    private final RedisCacheUtil redisCacheUtil;

    public void invalidate(String albumCode) {
        redisCacheUtil.incrementStrict(String.format(VERSION_KEY, albumCode));
        redisCacheUtil.deletePatternStrict(String.format(PHOTO_KEY_PATTERN, albumCode));
    }
}
