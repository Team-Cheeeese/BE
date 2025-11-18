package com.cheeeese.album.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AlbumExpirationRedisRepository {

    private static final String EXPIRATION_ZSET_KEY = "expired:album:zset";

    @Qualifier("cacheRedisTemplate")
    private final RedisTemplate<String, Object> cacheRedisTemplate;

    /**
     * ZSET에 앨범 ID와 만료 시간을 Score로 등록
     * Score는 Unix Timestamp(밀리초)로 사용
     * @param albumId 앨범 고유 ID
     * @param expiredAt 앨범 만료 시각 (LocalDateTime)
     */
    public void registerAlbum(Long albumId, LocalDateTime expiredAt) {
        // LocalDateTime을 Unix Timestamp (ms)로 변환
        long expirationMillis = expiredAt.toInstant(ZoneOffset.UTC).toEpochMilli();

        // ZADD key score member
        cacheRedisTemplate.opsForZSet().add(EXPIRATION_ZSET_KEY, albumId.toString(), (double) expirationMillis);
    }

    /**
     * 현재 시각을 기준으로 만료된 앨범 ID 목록만 ZSET에서 조회 (O(log N + k))
     * @return 만료된 앨범 ID Set
     */
    public Set<Long> getExpiredAlbumIds() {
        // 현재 시각의 Unix Timestamp (밀리초)
        long currentTimestamp = LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli();

        // ZRANGEBYSCORE key min max: Score가 0부터 현재 시각까지인 모든 Member를 조회
        Set<Object> members = cacheRedisTemplate.opsForZSet().rangeByScore(EXPIRATION_ZSET_KEY, 0, currentTimestamp);

        if (members == null || members.isEmpty()) {
            return Collections.emptySet();
        }

        return members.stream()
                .map(Object::toString)
                .map(Long::valueOf)
                .collect(Collectors.toSet());
    }

    /**
     * ZSET에서 만료 처리된 앨범을 제거 (O(log N))
     * @param albumId 앨범 고유 ID
     */
    public void unregister(Long albumId) {
        cacheRedisTemplate.opsForZSet().remove(EXPIRATION_ZSET_KEY, albumId.toString());
    }
}
