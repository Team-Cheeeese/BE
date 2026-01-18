package com.cheeeese.photo.application.logger;

import com.cheeeese.global.logging.LogMaskingUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class PhotoLogger {

    private static final String STAT_PREFIX = "[STAT]";

    private final LogMaskingUtil logMaskingUtil;

    /**
     * [지표 2, 3] 사진 업로드 완료
     * photo_upload_completed(user_id, album_code, photo_count, photo_id, created_at)
     */
    public void logUploadCompleted(Long userId, String albumCode, int photoCount, Long photoId) {
        try {
            MDC.put("type", "photo");
            log.info("{} photo_upload_completed | user_key={} album_code={} photo_count={} photo_id={} created_at={}",
                    STAT_PREFIX,
                    logMaskingUtil.userKey(userId),
                    albumCode,
                    photoCount,
                    photoId,
                    LocalDateTime.now()
            );
        } finally {
            MDC.remove("type");
        }
    }
}