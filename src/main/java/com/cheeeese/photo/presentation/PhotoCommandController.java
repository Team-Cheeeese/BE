package com.cheeeese.photo.presentation;

import com.cheeeese.global.common.CommonResponse;
import com.cheeeese.global.util.CurrentUser;
import com.cheeeese.photo.application.PhotoService;
import com.cheeeese.photo.presentation.swagger.PhotoCommandSwagger;
import com.cheeeese.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.cheeeese.global.common.code.SuccessCode.PHOTO_DELETE_SUCCESS;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/album/{code}/photo")
public class PhotoCommandController implements PhotoCommandSwagger {

    private final PhotoService photoService;

    @Override
    @DeleteMapping("/{photoId}")
    public CommonResponse<Void> deletePhoto(
            @CurrentUser User user,
            @PathVariable String code,
            @PathVariable Long photoId
    ) {
        photoService.deletePhoto(user, code, photoId);
        return CommonResponse.success(PHOTO_DELETE_SUCCESS);
    }
}
