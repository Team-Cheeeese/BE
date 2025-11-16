package com.cheeeese.photo.application;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class PresignedUrlService {

    private final S3Presigner s3Presigner;

    @Value("${ncp.object-storage.bucket}")
    private String bucket;

    public String generatePresignedPutUrl(String uniqueKey, String contentType) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(uniqueKey)
                .contentType(contentType)
                .build();

        PresignedPutObjectRequest presignedRequest =
                s3Presigner.presignPutObject(p -> p
                        .signatureDuration(Duration.ofMinutes(10))
                        .putObjectRequest(putObjectRequest)
                );

        return presignedRequest.url().toString();
    }

    public String generatePresignedGetUrl(String uniqueKey) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(uniqueKey)
                .build();

        PresignedGetObjectRequest presignedRequest =
                s3Presigner.presignGetObject(r -> r
                        .signatureDuration(Duration.ofMinutes(10))
                        .getObjectRequest(getObjectRequest)
                );

        return presignedRequest.url().toString();
    }
}
