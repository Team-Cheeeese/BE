package com.cheeeese.photo;

import com.cheeeese.global.util.RedisCacheUtil;
import com.cheeeese.photo.application.PhotoCacheInvalidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.inOrder;

@ExtendWith(MockitoExtension.class)
class PhotoCacheInvalidatorTest {

    @InjectMocks
    private PhotoCacheInvalidator photoCacheInvalidator;

    @Mock
    private RedisCacheUtil redisCacheUtil;

    @Test
    void invalidate_incrementsVersionAndDeletesPhotoCache() {
        String albumCode = "album-code";

        photoCacheInvalidator.invalidate(albumCode);

        var inOrder = inOrder(redisCacheUtil);
        inOrder.verify(redisCacheUtil).incrementStrict("cache:album:album-code:version");
        inOrder.verify(redisCacheUtil).deletePatternStrict("cache:album:album-code:photos:*");
    }
}
