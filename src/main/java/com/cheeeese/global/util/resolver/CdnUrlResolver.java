package com.cheeeese.global.util.resolver;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CdnUrlResolver {

    @Value("${cdn.original-domain}")
    private String originalDomain;

    @Value("${cdn.thumbnail-domain}")
    private String thumbnailDomain;

    @Value("${cdn.4cut-domain}")
    private String cutDomain;

    public String resolveOriginal(String path) {
        return resolve(originalDomain, path);
    }

    public String resolveThumbnail(String path) {
        return resolve(thumbnailDomain, path);
    }

    public String resolveCut(String path) {
        return resolve(cutDomain, path);
    }

    private String resolve(String domain, String path) {
        if (path == null || path.isBlank()) return null;
        if (path.startsWith("http")) return path;
        if (path.startsWith("/")) path = path.substring(1);
        return domain + "/" + path;
    }
}
