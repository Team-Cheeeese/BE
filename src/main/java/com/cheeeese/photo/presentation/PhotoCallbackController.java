package com.cheeeese.photo.presentation;

import com.cheeeese.global.common.CommonResponse;
import com.cheeeese.photo.dto.request.PhotoCompleteRequest;
import com.cheeeese.photo.application.PhotoCallbackService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static com.cheeeese.global.common.code.SuccessCode.THUMBNAIL_PRODUCE_COMPLETE;

@RestController
@RequestMapping("/internal/thumbnail")
@RequiredArgsConstructor
public class PhotoCallbackController {

    private final PhotoCallbackService photoCallbackService;

    @PostMapping("/complete")
    public CommonResponse<Void> completeUpload(@Valid @RequestBody PhotoCompleteRequest request) {
        photoCallbackService.markUploadCompleted(request);
        return CommonResponse.success(THUMBNAIL_PRODUCE_COMPLETE);
    }
}
