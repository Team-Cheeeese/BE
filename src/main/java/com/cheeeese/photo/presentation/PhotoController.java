package com.cheeeese.photo.presentation;

import com.cheeeese.global.common.CommonResponse;
import com.cheeeese.global.util.CurrentUser;
import com.cheeeese.photo.application.PhotoService;
import com.cheeeese.photo.dto.request.PhotoDownloadRequest;
import com.cheeeese.photo.dto.request.PhotoPresignedUrlRequest;
import com.cheeeese.photo.dto.request.PhotoUploadReportRequest;
import com.cheeeese.photo.dto.response.PhotoDownloadResponse;
import com.cheeeese.photo.dto.response.PhotoPresignedUrlResponse;
import com.cheeeese.photo.presentation.swagger.PhotoSwagger;
import com.cheeeese.user.domain.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static com.cheeeese.global.common.code.SuccessCode.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/photo")
public class PhotoController implements PhotoSwagger {

    private final PhotoService photoService;

    @Override
    @PostMapping("/presigned-url")
    public CommonResponse<PhotoPresignedUrlResponse> createPresignedUrls(
            @CurrentUser User user,
            @RequestBody @Valid PhotoPresignedUrlRequest request
    ) {
        return CommonResponse.success(PRESIGNED_URL_ISSUE_SUCCESS, photoService.createPresignedUrls(user, request));
    }

    @Override
    @PostMapping("/report")
    public CommonResponse<Void> reportUploadResult(
            @CurrentUser User user,
            @RequestBody @Valid PhotoUploadReportRequest request
    ) {
        photoService.reportUploadResult(user, request);
        return CommonResponse.success(PHOTO_UPLOAD_REPORT_SUCCESS);
    }

    @Override
    @PostMapping("/download-url")
    public CommonResponse<PhotoDownloadResponse> getDownloadPresignedUrls(
            @CurrentUser User user,
            @RequestBody @Valid PhotoDownloadRequest request
    ) {
        return CommonResponse.success(PRESIGNED_URL_ISSUE_SUCCESS, photoService.getDownloadPresignedUrls(user, request));
    }

    @Override
    @PostMapping("/{photoId}/liked")
    public CommonResponse<Void> createPhotoLikes(@CurrentUser User user, @PathVariable Long photoId) {
        photoService.createPhotoLikes(user, photoId);
        return CommonResponse.success(PHOTO_LIKES_CREATE_SUCCESS);
    }

    @Override
    @DeleteMapping("/{photoId}/unliked")
    public CommonResponse<Void> deletePhotoLikes(@CurrentUser User user, @PathVariable Long photoId) {
        photoService.deletePhotoLikes(user, photoId);
        return CommonResponse.success(PHOTO_LIKES_DELETE_SUCCESS);
    }
}