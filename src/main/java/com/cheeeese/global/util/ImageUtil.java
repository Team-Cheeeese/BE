package com.cheeeese.global.util;

import com.cheeeese.photo.exception.PhotoException;
import com.cheeeese.photo.exception.code.PhotoErrorCode;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Component;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Base64;

@Component
public class ImageUtil {
    private static final int CONNECTION_TIMEOUT_MS = 10_000;
    private static final int READ_TIMEOUT_MS = 30_000;
    // URL로부터 이미지를 가져와 리사이징 및 Base64 인코딩
    public String resizeAndEncodeToBase64FromUrl(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) {
            throw new PhotoException(PhotoErrorCode.IMAGE_PROCESSING_FAILED);
        }

        HttpURLConnection conn = null;
        try {
            URL url = URI.create(imageUrl).toURL();
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(CONNECTION_TIMEOUT_MS);
            conn.setReadTimeout(READ_TIMEOUT_MS);
            try (InputStream is = conn.getInputStream();
                 ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

                Thumbnails.of(is)
                        .size(1024, 1024)
                        .outputFormat("jpg")
                        .outputQuality(0.8)
                        .toOutputStream(outputStream);
                byte[] imageBytes = outputStream.toByteArray();

                return Base64.getEncoder().encodeToString(imageBytes);
            }
        } catch (IOException | IllegalArgumentException e) {
            throw new PhotoException(PhotoErrorCode.IMAGE_PROCESSING_FAILED);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }
}