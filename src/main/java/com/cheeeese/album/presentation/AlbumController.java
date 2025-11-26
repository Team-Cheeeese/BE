package com.cheeeese.album.presentation;

import com.cheeeese.album.application.AlbumService;
import com.cheeeese.album.dto.request.AlbumCreationRequest;
import com.cheeeese.album.dto.response.*;
import com.cheeeese.album.presentation.swagger.AlbumSwagger;
import com.cheeeese.global.common.CommonResponse;
import com.cheeeese.global.util.CurrentUser;
import com.cheeeese.album.dto.response.AlbumBest4CutResponse;
import com.cheeeese.user.domain.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.cheeeese.global.common.code.SuccessCode.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/album")
public class AlbumController implements AlbumSwagger {

    private final AlbumService albumService;

    @Override
    @PostMapping
    public CommonResponse<AlbumCreationResponse> createAlbum(
            @CurrentUser User user,
            @RequestBody @Valid AlbumCreationRequest request
    ) {
         return CommonResponse.success(ALBUM_CREATE_SUCCESS, albumService.createAlbum(user, request));
    }

    @Override
    @GetMapping("/{code}/invitation")
    public CommonResponse<AlbumInvitationResponse> getInvitationInfo(@PathVariable String code) {
        return CommonResponse.success(ALBUM_INVITATION_FETCH_SUCCESS, albumService.getInvitationInfo(code));
    }

    @Override
    @PostMapping("/{code}/enter")
    public CommonResponse<AlbumEnterResponse> enterAlbum(
            @CurrentUser User user,
            @PathVariable String code
    ) {
        AlbumEnterResponse response = albumService.enterAlbum(code, user);
        return CommonResponse.success(ALBUM_ENTER_SUCCESS, response);
    }

    @Override
    @GetMapping("/{code}/available-count")
    public CommonResponse<UploadAvailableCountResponse> getAvailableUploadCount(@PathVariable String code) {
        return CommonResponse.success(PHOTO_AVAILABLE_COUNT_FETCH_SUCCESS, albumService.getAvailablePhotoCount(code));
    }

    @Override
    @GetMapping("/{code}/participants")
    public CommonResponse<AlbumParticipantResponse> getAlbumParticipants(
            Authentication authentication,
            @PathVariable String code
    ) {
      return CommonResponse.success(
              ALBUM_PARTICIPANT_FETCH_SUCCESS,
              albumService.getAlbumParticipantList(authentication, code)
      );
    }

    @Override
    @GetMapping("/{code}/info")
    public CommonResponse<AlbumInfoResponse> getAlbumInfo(@PathVariable String code) {
        return CommonResponse.success(ALBUM_INFO_GET_SUCCESS, albumService.getAlbumInfo(code));
    }

    @Override
    @GetMapping("/{code}/best-4cut")
    public CommonResponse<List<AlbumBest4CutResponse>> getAlbumBest4Cut(
            @CurrentUser User user,
            @PathVariable String code
    ) {
        return CommonResponse.success(
                ALBUM_BEST4CUT_GET_SUCCESS,
                albumService.getAlbumBest4Cut(user, code)
        );
    }

    @Override
    @DeleteMapping("/{code}/participants/me")
    public CommonResponse<Void> leaveUser(@CurrentUser User user, @PathVariable String code) {
        albumService.leaveUser(user, code);
        return CommonResponse.success(ALBUM_LEAVE_SUCCESS);
    }
}