package com.cheeeese.photo.application.logger;

import com.cheeeese.global.logging.LogMaskingUtil;
import com.cheeeese.photo.domain.Photo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

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

    /**
     * [지표 3, 5] 사진 다운로드
     * photo_download_log(user_id, album_code, photo_ids, requested_at)
     */
    public void logDownload(Long userId, String albumCode, int downloaderCount) {
        try {
            MDC.put("type", "photo");
            log.info("{} photo_download_log | user_key={} album_code={} downloader_count={} requested_at={}",
                    STAT_PREFIX,
                    logMaskingUtil.userKey(userId),
                    albumCode,
                    downloaderCount,
                    LocalDateTime.now()
            );
        } finally {
            MDC.remove("type");
        }
    }

    /**
     * [지표] 고유 좋아요 사용자 누른 사람 수
     * liker_count (앨범당 각 사용자의 첫 좋아요 시)
     */
    public void logFirstLike(Long userId, Long albumId, int likerCount) {
        try {
            MDC.put("type", "album");
            log.info("{} album_first_liked | user_key={} album_id={} liker_count={}",
                    STAT_PREFIX,
                    logMaskingUtil.userKey(userId),
                    albumId,
                    likerCount
            );
        } finally {
            MDC.remove("type");
        }
    }
}