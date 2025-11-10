package com.cheeeese.album.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AlbumExpirationRedisRepository {

    private static final String TRACKING_KEY = "expired:album:tracking";
    private static final Duration ALBUM_TTL = Duration.ofDays(7);

    @Qualifier("cacheRedisTemplate")
    private final RedisTemplate<String, Object> cacheRedisTemplate;

    public void registerAlbum(Long albumId) {
        String key = buildAlbumKey(albumId);
        cacheRedisTemplate.opsForValue().set(key, albumId.toString(), ALBUM_TTL);
        cacheRedisTemplate.opsForSet().add(TRACKING_KEY, albumId.toString());
    }

    public Set<Long> getTrackedAlbumIds() {
        Set<Object> members = cacheRedisTemplate.opsForSet().members(TRACKING_KEY);
        if (members == null || members.isEmpty()) {
            return Collections.emptySet();
        }
        return members.stream()
                .map(Object::toString)
                .map(Long::valueOf)
                .collect(Collectors.toSet());
    }

    public boolean isExpired(Long albumId) {
        Long ttl = cacheRedisTemplate.getExpire(buildAlbumKey(albumId), TimeUnit.SECONDS);
        if (ttl == null) {
            return true;
        }

        if (ttl == -2) {
            return true;
        }

        if (ttl == -1) {
            return false;
        }

        return ttl <= 0;
    }

    public void unregister(Long albumId) {
        cacheRedisTemplate.opsForSet().remove(TRACKING_KEY, albumId.toString());
    }

    private String buildAlbumKey(Long albumId) {
        return "expired:album:" + albumId;
    }
}
