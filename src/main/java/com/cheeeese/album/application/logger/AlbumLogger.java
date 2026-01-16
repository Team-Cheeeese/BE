package com.cheeeese.album.application.logger;

import com.cheeeese.album.domain.type.Role;
import com.cheeeese.global.logging.LogMaskingUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class AlbumLogger {

    private static final String STAT_PREFIX = "[STAT]";

    private final LogMaskingUtil logMaskingUtil;

    /**
     * [지표 1] 앨범 생성
     * album_created(user_id, album_code, invitable_count, created_at)
     */
    public void logAlbumCreated(Long userId, String albumCode, int invitableCount) {
        try {
            MDC.put("type", "album");
            log.info("{} album_created | user_key={} album_code={} invitable_count={} created_at={}",
                    STAT_PREFIX,
                    logMaskingUtil.userKey(userId),
                    albumCode,
                    invitableCount,
                    LocalDateTime.now()
            );
        } finally {
            MDC.remove("type");
        }
    }

    /**
     * [지표 1, 2] 앨범 입장 (신규)
     * album_joined(user_id, album_code, visitor_count, joined_at)
     */
    public void logAlbumJoined(Long userId, String albumCode, int participant, boolean photoExistOnJoin) {
        try {
            MDC.put("type", "album");
            log.info("{} album_joined | user_key={} album_code={} visitor_count={} joined_at={}",
                    STAT_PREFIX,
                    logMaskingUtil.userKey(userId),
                    albumCode,
                    participant,
                    LocalDateTime.now()
            );
        } finally {
            MDC.remove("type");
        }
    }

    /**
     * [지표 2, 4] 앨범 재방문 (조회)
     * album_view_start(user_id, album_code, role, viewed_at)
     */
    public void logAlbumViewed(Long userId, String albumCode, Role role) {
        try {
            MDC.put("type", "album");
            log.info("{} album_view_start | user_key={} album_code={} role={} viewed_at={}",
                    STAT_PREFIX, logMaskingUtil.userKey(userId), albumCode, role, LocalDateTime.now());
        } finally {
            MDC.remove("type");
        }
    }

    /**
     * [지표] 앨범 방문자 수 2명 도달
     * album_id, album_code. participant_count, achieved_at
     */
    public void logParticipants2MilestoneAt(Long albumId, String albumCode, LocalDateTime achievedAt) {
        try {
            MDC.put("type", "album");
            log.info("{} album_participants_milestone_at | album_id={} album_code={} participant_count=2 achieved_at={}",
                    STAT_PREFIX, albumId, albumCode, achievedAt);
        } finally {
            MDC.clear();
        }
    }
}