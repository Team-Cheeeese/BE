package com.cheeeese.global.util;

import com.cheeeese.photo.exception.PhotoException;
import com.cheeeese.photo.exception.code.PhotoErrorCode;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Component;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Base64;

@Component
public class ImageUtil {
    // URL로부터 이미지를 가져와 리사이징 및 Base64 인코딩
    public String resizeAndEncodeToBase64FromUrl(String imageUrl) {
        try (InputStream is = URI.create(imageUrl).toURL().openStream();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            Thumbnails.of(is)
                    .size(1024, 1024)
                    .outputFormat("jpg")
                    .outputQuality(0.8)
                    .toOutputStream(outputStream);

            byte[] imageBytes = outputStream.toByteArray();
            String b64 = Base64.getEncoder().encodeToString(imageBytes);

            return b64;
        } catch (IOException e) {
            throw new PhotoException(PhotoErrorCode.IMAGE_PROCESSING_FAILED);
        }
    }
}