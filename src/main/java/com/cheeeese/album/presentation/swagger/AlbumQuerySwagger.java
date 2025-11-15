package com.cheeeese.album.presentation.swagger;

import com.cheeeese.album.dto.response.ClosedAlbumPageResponse;
import com.cheeeese.album.dto.response.OpenAlbumPageResponse;
import com.cheeeese.global.common.CommonResponse;
import com.cheeeese.global.util.CurrentUser;
import com.cheeeese.user.domain.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "[마이페이지-앨범-조회]", description = "앨범 목록 조회 API")
@RequestMapping("/v1/album")
public interface AlbumQuerySwagger {

    @Operation(
            summary = "열린 앨범 전체 조회",
            description = "사용자가 참여 중인 모든 열린 앨범을 만료 임박 순으로 조회합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "열린 앨범 목록 조회 성공")
    })
    @GetMapping("/open")
    CommonResponse<OpenAlbumPageResponse> getOpenAlbums(
            @CurrentUser User user,
            @Parameter(description = "페이지 번호", schema = @Schema(defaultValue = "0"))
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", schema = @Schema(defaultValue = "2"))
            @RequestParam(defaultValue = "2") int size
    );

    @Operation(
            summary = "열린 앨범 중 내가 만든 앨범 조회",
            description = "사용자가 메이커인 열린 앨범을 만료 임박 순으로 조회합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "내가 만든 열린 앨범 목록 조회 성공")
    })
    @GetMapping("/open/me")
    CommonResponse<OpenAlbumPageResponse> getMyOpenAlbums(
            @CurrentUser User user,
            @Parameter(description = "페이지 번호", schema = @Schema(defaultValue = "0"))
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", schema = @Schema(defaultValue = "2"))
            @RequestParam(defaultValue = "2") int size
    );

    @Operation(
            summary = "닫힌 앨범 목록 조회",
            description = "사용자가 참여했던 닫힌 앨범을 생성일 최신 순으로 조회합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "닫힌 앨범 목록 조회 성공")
    })
    @GetMapping("/closed")
    CommonResponse<ClosedAlbumPageResponse> getClosedAlbums(
            @CurrentUser User user,
            @Parameter(description = "페이지 번호", schema = @Schema(defaultValue = "0"))
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", schema = @Schema(defaultValue = "6"))
            @RequestParam(defaultValue = "6") int size
    );
}
