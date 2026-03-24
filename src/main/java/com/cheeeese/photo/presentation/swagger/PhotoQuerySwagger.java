package com.cheeeese.photo.presentation.swagger;

import com.cheeeese.album.domain.type.AlbumSorting;
import com.cheeeese.global.common.CommonResponse;
import com.cheeeese.global.util.CurrentUser;
import com.cheeeese.photo.dto.response.*;
import com.cheeeese.user.domain.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "[사진 조회]", description = "사진 조회 관련 API")
public interface PhotoQuerySwagger {
    @Operation(
            summary = "앨범 내 사진 목록 조회 API",
            description = """
                    ### PathVariable
                    ---
                    `code`: 앨범 코드 \n
                    
                    ### RequestParam
                    ---
                    `page`: 조회할 페이지 번호 (기본값: 0) \n
                    `size`: 페이지당 사진 개수 (기본값: 20) \n
                    `sorting`: 정렬 기준 (`CREATED_AT`: 업로드 시간순, `POPULAR`: 띱 많은순, `CAPTURED_AT`: 최근 촬영한 시간순)
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "앨범 내 사진 목록 조회가 성공적으로 실행되었습니다."
            )
    })
    CommonResponse<PhotoPageResponse> getAlbumPhotoPage(
            @CurrentUser User user,
            @PathVariable String code,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "CREATED_AT") AlbumSorting sorting,
            @RequestParam(defaultValue = "false") boolean isMine
    );

    @Operation(
            summary = "내가 띱한 사진 목록 조회 API",
            description = """
                    ### PathVariable
                    ---
                    `code`: 앨범 코드 \n
                    
                    ### RequestParam
                    ---
                    `page`: 조회할 페이지 번호 (기본값: 0) \n
                    `size`: 페이지당 사진 개수 (기본값: 10) \n
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "내가 띱한 사진 목록 조회가 성공적으로 실행되었습니다."
            )
    })
    CommonResponse<PhotoLikedPageResponse> getAlbumLikedPhotoPage(
            @CurrentUser User user,
            @PathVariable String code,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    );

    @Operation(
            summary = "앨범 내 사진 상세 조회 API",
            description = """
                    ### PathVariable
                    ---
                    `code`: 앨범 코드 \n
                    `photoId`: 사진 ID
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "앨범 내 사진 상세 조회가 성공적으로 실행되었습니다."
            )
    })
    CommonResponse<PhotoDetailResponse> getPhotoDetail(
            @CurrentUser User user,
            @PathVariable String code,
            @PathVariable Long photoId
    );

    @Operation(
            summary = "띱한 사용자 목록 조회 API",
            description = """
                    ### PathVariable
                    ---
                    `code`: 앨범 코드 (String) \n
                    `photoId`: 사진 고유 ID (Long)
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "띱한 사용자 목록 조회가 성공적으로 실행되었습니다."
            )
    })
    CommonResponse<PhotoLikedUserResponse> getPhotoLikedUsers(
            @CurrentUser User user,
            @PathVariable String code,
            @PathVariable Long photoId
    );
}
