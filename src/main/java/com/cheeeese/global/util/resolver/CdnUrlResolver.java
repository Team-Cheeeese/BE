package com.cheeeese.global.util.resolver;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CdnUrlResolver {

    private static final List<String> PREFIXES = List.of(
            "say-cheeeese/",
            "say-cheeeese-thumbnail/",
            "say-cheeeese-profile/"
    );

    @Value("${cdn.original-domain}")
    private String originalDomain;

    @Value("${cdn.thumbnail-domain}")
    private String thumbnailDomain;

    @Value("${cdn.profile-domain}")
    private String profileDomain;

    public String resolveOriginal(String path) {
        return resolve(originalDomain, path);
    }

    public String resolveThumbnail(String path) {
        return resolve(thumbnailDomain, path);
    }

    public String resolveProfile(String path) {
        return resolve(profileDomain, path);
    }

    private String resolve(String domain, String path) {
        if (path == null || path.isBlank()) return null;
        if (path.startsWith("http")) return path;

        for (String prefix : PREFIXES) {
            if (path.startsWith(prefix)) {
                path = path.substring(prefix.length());
                break;
            }
        }

        if (path.startsWith("/")) path = path.substring(1);
        return domain + "/" + path;
    }
}
