package com.cheeeese.photo.presentation;

import com.cheeeese.album.domain.type.AlbumSorting;
import com.cheeeese.global.common.CommonResponse;
import com.cheeeese.global.util.CurrentUser;
import com.cheeeese.photo.application.PhotoQueryService;
import com.cheeeese.photo.dto.response.PhotoDetailResponse;
import com.cheeeese.photo.dto.response.PhotoPageResponse;
import com.cheeeese.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static com.cheeeese.global.common.code.SuccessCode.PHOTO_DETAIL_GET_SUCCESS;
import static com.cheeeese.global.common.code.SuccessCode.PHOTO_LIST_GET_SUCCESS;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/album")
public class PhotoQueryController {

    private final PhotoQueryService photoQueryService;

    @GetMapping("/{code}/photos")
    public CommonResponse<PhotoPageResponse> getAlbumPhotoPage(
            @CurrentUser User user,
            @PathVariable String code,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "CREATED_AT") AlbumSorting sorting
    ) {
        return CommonResponse.success(
                PHOTO_LIST_GET_SUCCESS,
                photoQueryService.getPhotoPage(user, code, page, size, sorting)
        );
    }

    @GetMapping("/{code}/photos/{photoId}")
    public CommonResponse<PhotoDetailResponse> getPhotoDetail(
            @CurrentUser User user,
            @PathVariable String code,
            @PathVariable Long photoId
    ) {
        return CommonResponse.success(
                PHOTO_DETAIL_GET_SUCCESS,
                photoQueryService.getPhotoDetail(user, code, photoId)
        );
    }
}
