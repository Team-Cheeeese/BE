package com.cheeeese.photo;

import com.cheeeese.global.config.RetryConfig;
import com.cheeeese.photo.application.PhotoCacheInvalidationEvent;
import com.cheeeese.photo.application.PhotoCacheInvalidationEventHandler;
import com.cheeeese.photo.application.PhotoCacheInvalidator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringJUnitConfig(classes = {
        RetryConfig.class,
        PhotoCacheInvalidationEventHandler.class
})
class PhotoCacheInvalidationEventHandlerTest {

    @MockitoBean
    private PhotoCacheInvalidator photoCacheInvalidator;

    @Autowired
    private PhotoCacheInvalidationEventHandler handler;

    @Test
    void handle_retriesThreeTimesAndRecovers() {
        String albumCode = "album-code";
        doThrow(new IllegalStateException("Redis unavailable"))
                .when(photoCacheInvalidator).invalidate(albumCode);

        assertDoesNotThrow(() -> handler.handle(new PhotoCacheInvalidationEvent(albumCode)));

        verify(photoCacheInvalidator, times(3)).invalidate(albumCode);
    }
}
