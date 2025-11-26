package com.cheeeese.photo.application.logger;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
public class PhotoLogger {

    private static final String STAT_PREFIX = "[STAT]";

    /**
     * [지표 2, 3] 사진 업로드 완료
     * photo_upload_completed(user_id, album_code, photo_id, created_at)
     */
    public void logUploadCompleted(Long userId, String albumCode, Long photoId) {
        try {
            MDC.put("type", "photo");
            log.info("{} photo_upload_completed | user_id={} album_code={} photo_id={} created_at={}",
                    STAT_PREFIX, userId, albumCode, photoId, LocalDateTime.now());
        } finally {
            MDC.remove("type");
        }
    }

    /**
     * [지표 3, 5] 사진 다운로드
     * photo_download_log(user_id, album_code, photo_ids, requested_at)
     */
    public void logDownload(Long userId, String albumCode, List<Long> photoIds) {
        try {
            MDC.put("type", "photo");
            log.info("{} photo_download_log | user_id={} album_code={} photo_ids={} requested_at={}",
                    STAT_PREFIX, userId, albumCode, photoIds, LocalDateTime.now());
        } finally {
            MDC.remove("type");
        }
    }
}