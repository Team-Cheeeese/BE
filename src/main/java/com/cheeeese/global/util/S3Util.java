package com.cheeeese.global.util;

import java.net.URI;
import java.net.URISyntaxException;

public class S3Util {

    public static String extractObjectKey(String imageUrl) {
        if (imageUrl == null) {
            throw new NullPointerException("image url is null");
        }

        try {
            URI uri = new URI(imageUrl);
            String path = uri.getPath();
            if (path != null && !path.isBlank()) {
                return path.startsWith("/") ? path.substring(1) : path;
            }
        } catch (URISyntaxException e) {

        }
        if (imageUrl.startsWith("album/")) {
            return imageUrl;
        }
        return imageUrl;
    }

    public static String extractFileName(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) {
            return "unnamed.jpg";
        }
        String normalized = imageUrl.replace('\\', '/');

        int lastSlashIdx = normalized.lastIndexOf('/');
        String fileName = (lastSlashIdx >= 0)
                ? normalized.substring(lastSlashIdx + 1)
                : normalized;

        int underscoreIdx = fileName.indexOf('_');
        if (underscoreIdx >= 0 && underscoreIdx < fileName.length() - 1) {
            fileName = fileName.substring(underscoreIdx + 1);
        }

        return fileName;
    }
}
