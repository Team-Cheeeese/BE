package com.cheeeese.global.logging;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

@Component
public class LogMaskingUtil {

    @Value("${log.hash.salt}")
    private String salt;

    public String userKey(Long userId) {
        if (userId == null) {
            return "unknown";
        }
        return maskIdentifier(String.valueOf(userId));
    }

    public String maskIdentifier(String raw) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest((raw + salt).getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hashed).substring(0, 12);
        } catch (NoSuchAlgorithmException e) {
            return "unknown";
        }
    }
}
