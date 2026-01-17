package com.cheeeese.cheese4cut.application.logger;

import com.cheeeese.global.logging.LogMaskingUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class Cheese4cutLogger {

    private static final String STAT_PREFIX = "[STAT]";

    private final LogMaskingUtil logMaskingUtil;

    public void logCheese4CutFinalized(Long userId, List<Long> photoIds, String albumCode) {
        try {
            MDC.put("type", "cheese4cut");
            log.info("[Cheese4cut] Cheese4cut Finalized | album_code={} maker_id={} photo_ids={} finalized_at={} 4cut_created=true",
                    albumCode, logMaskingUtil.userKey(userId), photoIds, LocalDateTime.now());
        } finally {
            MDC.remove("type");
        }
    }

    public void logCheese4CutAutoCreated(String albumCode) {
        try {
            MDC.put("type", "cheese4cut");
            log.info("[Cheese4cut] Cheese4cut created automatically | album_code={} created_at={}",
                    albumCode, LocalDateTime.now());
        } finally {
            MDC.remove("type");
        }
    }
}
