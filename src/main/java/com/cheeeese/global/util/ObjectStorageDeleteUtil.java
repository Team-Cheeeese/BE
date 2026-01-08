package com.cheeeese.global.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ObjectStorageDeleteUtil {

    private final S3Client s3Client;

    @Value("${ncp.object-storage.bucket}")
    private String originalBucket;

    @Value("${ncp.object-storage.thumbnail-bucket}")
    private String thumbnailBucket;

    public void deletePhotoObjects(String imageUrl, String thumbnailUrl) {
        deleteObjectIfPresent(originalBucket, extractObjectKey(imageUrl, "say-cheeeese/"));

        List<String> thumbnailKeys = buildThumbnailKeys(thumbnailUrl);
        for (String key : thumbnailKeys) {
            deleteObjectIfPresent(thumbnailBucket, key);
        }
    }

    private void deleteObjectIfPresent(String bucket, String objectKey) {
        if (objectKey == null || objectKey.isBlank()) {
            return;
        }
        try {
            s3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(objectKey)
                    .build());
        } catch (Exception exception) {
            log.warn("[ObjectStorage] Failed to delete object bucket={} key={}", bucket, objectKey, exception);
        }
    }

    private List<String> buildThumbnailKeys(String thumbnailUrl) {
        String baseKey = extractObjectKey(thumbnailUrl, "say-cheeeese-thumbnail/");
        if (baseKey == null || baseKey.isBlank()) {
            return List.of();
        }

        String normalizedBaseKey = baseKey;
        if (normalizedBaseKey.endsWith("_250.webp")) {
            normalizedBaseKey = normalizedBaseKey.replace("_250.webp", ".webp");
        } else if (normalizedBaseKey.endsWith("_100.webp")) {
            normalizedBaseKey = normalizedBaseKey.replace("_100.webp", ".webp");
        } else if (normalizedBaseKey.endsWith("_60.webp")) {
            normalizedBaseKey = normalizedBaseKey.replace("_60.webp", ".webp");
        }

        if (!normalizedBaseKey.endsWith(".webp")) {
            return List.of(normalizedBaseKey);
        }

        String baseWithoutExt = normalizedBaseKey.substring(0, normalizedBaseKey.length() - ".webp".length());
        return List.of(
                normalizedBaseKey,
                baseWithoutExt + "_250.webp",
                baseWithoutExt + "_100.webp",
                baseWithoutExt + "_60.webp"
        );
    }

    private String extractObjectKey(String rawUrl, String removablePrefix) {
        if (rawUrl == null || rawUrl.isBlank()) {
            return null;
        }

        String key = S3Util.extractObjectKey(rawUrl);
        if (key.startsWith(removablePrefix)) {
            key = key.substring(removablePrefix.length());
        }
        return key;
    }
}
