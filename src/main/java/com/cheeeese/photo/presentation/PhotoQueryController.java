package com.cheeeese.photo.presentation;

import com.cheeeese.album.domain.type.AlbumSorting;
import com.cheeeese.album.dto.response.AlbumInfoResponse;
import com.cheeeese.photo.dto.response.PhotoBest4CutResponse;
import com.cheeeese.global.common.CommonResponse;
import com.cheeeese.global.util.CurrentUser;
import com.cheeeese.photo.application.PhotoInfoService;
import com.cheeeese.photo.application.PhotoQueryService;
import com.cheeeese.photo.dto.response.*;
import com.cheeeese.photo.presentation.swagger.PhotoQuerySwagger;
import com.cheeeese.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.cheeeese.global.common.code.SuccessCode.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/album")
public class PhotoQueryController implements PhotoQuerySwagger {

    private final PhotoQueryService photoQueryService;
    private final PhotoInfoService photoInfoService;

    @Override
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

    @Override
    @GetMapping("/{code}/photos/liked")
    public CommonResponse<PhotoLikedPageResponse> getAlbumLikedPhotoPage(
            @CurrentUser User user,
            @PathVariable String code,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return CommonResponse.success(
                PHOTO_LIKES_LIST_GET_SUCCESS,
                photoQueryService.getPhotoLiked(user, code, page, size)
        );
    }

    @Override
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

    @GetMapping("/{code}/photos/{photoId}/liker")
    public CommonResponse<PhotoLikerResponse> getPhotoLikers(
            @CurrentUser User user,
            @PathVariable String code,
            @PathVariable Long photoId
    ) {
        return CommonResponse.success(
                PHOTO_LIKERS_GET_SUCCESS,
                photoInfoService.getPhotoLikers(user, code, photoId)
        );
    }

    @GetMapping("/{code}/best-4cut")
    public CommonResponse<List<PhotoBest4CutResponse>> getAlbumBest4Cut(
            @CurrentUser User user,
            @PathVariable String code
    ) {
        return CommonResponse.success(
                PHOTO_BEST4CUT_GET_SUCCESS,
                photoQueryService.getAlbumBest4Cut(user, code)
        );
    }
}
