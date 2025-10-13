package com.cheeeese.album.presentation;

import com.cheeeese.album.application.AlbumService;
import com.cheeeese.album.dto.response.AlbumInvitationResponse;
import com.cheeeese.album.presentation.swagger.AlbumSwagger;
import com.cheeeese.global.common.CommonResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.cheeeese.global.common.code.SuccessCode.ALBUM_INVITATION_FETCH_SUCCESS;


@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/album")
public class AlbumController implements AlbumSwagger {

    private final AlbumService albumService;

    @Override
    @GetMapping("/{code}/invitation")
    public CommonResponse<AlbumInvitationResponse> getInvitationInfo(@PathVariable String code) {
        return CommonResponse.success(ALBUM_INVITATION_FETCH_SUCCESS, albumService.getInvitationInfo(code));
    }
}