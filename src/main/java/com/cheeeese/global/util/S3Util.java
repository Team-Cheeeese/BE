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
}
