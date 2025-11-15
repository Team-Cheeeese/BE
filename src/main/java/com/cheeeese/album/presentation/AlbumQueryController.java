package com.cheeeese.album.presentation;

import com.cheeeese.album.application.AlbumQueryService;
import com.cheeeese.album.dto.response.ClosedAlbumPageResponse;
import com.cheeeese.album.dto.response.OpenAlbumPageResponse;
import com.cheeeese.album.presentation.swagger.AlbumQuerySwagger;
import com.cheeeese.global.common.CommonResponse;
import com.cheeeese.global.util.CurrentUser;
import com.cheeeese.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.cheeeese.global.common.code.SuccessCode.ALBUM_CLOSED_LIST_FETCH_SUCCESS;
import static com.cheeeese.global.common.code.SuccessCode.ALBUM_MY_OPEN_LIST_FETCH_SUCCESS;
import static com.cheeeese.global.common.code.SuccessCode.ALBUM_OPEN_LIST_FETCH_SUCCESS;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/album")
public class AlbumQueryController implements AlbumQuerySwagger {

    private final AlbumQueryService albumQueryService;

    @Override
    @GetMapping("/open")
    public CommonResponse<OpenAlbumPageResponse> getOpenAlbums(
            @CurrentUser User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "2") int size
    ) {
        return CommonResponse.success(
                ALBUM_OPEN_LIST_FETCH_SUCCESS,
                albumQueryService.getOpenAlbums(user, page, size)
        );
    }

    @Override
    @GetMapping("/open/me")
    public CommonResponse<OpenAlbumPageResponse> getMyOpenAlbums(
            @CurrentUser User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "2") int size
    ) {
        return CommonResponse.success(
                ALBUM_MY_OPEN_LIST_FETCH_SUCCESS,
                albumQueryService.getMyOpenAlbums(user, page, size)
        );
    }

    @Override
    @GetMapping("/closed")
    public CommonResponse<ClosedAlbumPageResponse> getClosedAlbums(
            @CurrentUser User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size
    ) {
        return CommonResponse.success(
                ALBUM_CLOSED_LIST_FETCH_SUCCESS,
                albumQueryService.getClosedAlbums(user, page, size)
        );
    }
}
