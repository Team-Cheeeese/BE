package com.cheeeese.photo.application.validator;

import com.cheeeese.photo.dto.request.PhotoPresignedUrlRequest;
import com.cheeeese.photo.exception.PhotoException;
import com.cheeeese.photo.exception.code.PhotoErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PhotoValidator {

    private static final long MAX_FILE_SIZE = 6 * 1024 * 1024; // 6MB
    private static final List<String> ALLOWED_TYPES = List.of("image/jpeg", "image/png", "image/jpg");

    /**
     * presigned URL 발급 전, 파일 정보 형식/용량 검증
     */
    public void validateFileInfos(List<PhotoPresignedUrlRequest.FileInfo> fileInfos) {
        if (fileInfos == null || fileInfos.isEmpty()) {
            throw new PhotoException(PhotoErrorCode.PHOTO_FILE_LIST_EMPTY);
        }

        for (PhotoPresignedUrlRequest.FileInfo file : fileInfos) {
            if (file.fileName() == null || file.fileName().isBlank()) {
                throw new PhotoException(PhotoErrorCode.PHOTO_FILE_NAME_REQUIRED);
            }

            if (file.fileSize() > MAX_FILE_SIZE) {
                throw new PhotoException(PhotoErrorCode.PHOTO_FILE_SIZE_EXCEEDED);
            }

            if (!ALLOWED_TYPES.contains(file.contentType())) {
                throw new PhotoException(PhotoErrorCode.PHOTO_INVALID_CONTENT_TYPE);
            }
        }
    }

    /**
     * 앨범 내 업로드 제한 검증
     */
    public void validatePhotoCount(int currentCount, int uploadCount, int maxPhotoCount) {
        if (currentCount + uploadCount > maxPhotoCount) {
            throw new PhotoException(PhotoErrorCode.PHOTO_MAX_COUNT_EXCEEDED);
        }
    }
}
