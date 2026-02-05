package com.cheeeese.album.application.logger;

import com.cheeeese.album.domain.type.Role;
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
     * album_joined(user_id, album_code, visitor_count, photo_exist_on_join, joined_at)
     */
    public void logAlbumJoined(Long userId, String albumCode, int participant, boolean photoExistOnJoin) {
        try {
            MDC.put("type", "album");
            log.info("{} album_joined | user_key={} album_code={} visitor_count={} photo_exist_on_join={} joined_at={}",
                    STAT_PREFIX,
                    logMaskingUtil.userKey(userId),
                    albumCode,
                    participant,
                    photoExistOnJoin,
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
     * [지표 3, 5] 사진 다운로드
     * album_download_log(user_id, album_code, downloaderCount)
     */
    public void logDownload(Long userId, String albumCode, int downloaderCount) {
        try {
            MDC.put("type", "album");
            log.info("{} album_download_log | user_key={} album_code={} downloader_count={}",
                    STAT_PREFIX,
                    logMaskingUtil.userKey(userId),
                    albumCode,
                    downloaderCount
            );
        } finally {
            MDC.remove("type");
        }
    }

    /**
     * [지표] 고유 좋아요 사용자 누른 사람 수
     * liker_count (앨범당 각 사용자의 첫 좋아요 시)
     */
    public void logFirstLike(Long userId, String albumCode, int likerCount) {
        try {
            MDC.put("type", "album");
            log.info("{} album_first_liked | user_key={} album_code={} liker_count={}",
                    STAT_PREFIX,
                    logMaskingUtil.userKey(userId),
                    albumCode,
                    likerCount
            );
        } finally {
            MDC.remove("type");
        }
    }

    /**
     * [지표] 앨범 방문자 수 2명 도달
     * album_id, participant_count, achieved_at
     */
    public void logParticipants2MilestoneAt(String albumCode, LocalDateTime achievedAt) {
        try {
            MDC.put("type", "album");
            log.info("{} album_participants_milestone_at | album_code={} achieved_at={}",
                    STAT_PREFIX, albumCode, achievedAt);
        } finally {
            MDC.remove("type");
        }
    }

    public void logCheese4CutFinalized(Long userId, List<Long> photoIds, String albumCode) {
        try {
            MDC.put("type", "album");
            log.info("{} Cheese4cut Finalized | album_code={} maker_id={} photo_ids={} created_at={} 4cut_created=true",
                    STAT_PREFIX, albumCode, logMaskingUtil.userKey(userId), photoIds, LocalDateTime.now());
        } finally {
            MDC.remove("type");
        }
    }

    public void logCheese4CutAutoCreated(String albumCode) {
        try {
            MDC.put("type", "album");
            log.info("{} Cheese4cut created automatically | album_code={} created_at={}",
                    STAT_PREFIX, albumCode, LocalDateTime.now());
        } finally {
            MDC.remove("type");
        }
    }
}