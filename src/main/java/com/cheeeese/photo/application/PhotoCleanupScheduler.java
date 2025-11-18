package com.cheeeese.photo.application;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PhotoCleanupScheduler {

    private final PhotoService photoService;

    @Scheduled(fixedDelay = 600000L)
    public void runCleanup() {
        photoService.cleanupOldUploadingPhotos();
    }
}
