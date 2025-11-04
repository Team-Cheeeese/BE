package com.cheeeese.photo.presentation.swagger;

import com.cheeeese.global.common.CommonResponse;
import com.cheeeese.global.util.CurrentUser;
import com.cheeeese.photo.dto.request.PhotoPresignedUrlRequest;
import com.cheeeese.photo.dto.request.PhotoUploadReportRequest;
import com.cheeeese.photo.dto.response.PhotoPresignedUrlResponse;
import com.cheeeese.user.domain.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "[사진]", description = "사진 업로드 및 관리에 대한 API")
public interface PhotoSwagger {
    @Operation(
            summary = "Presigned URL 발급 API",
            description = """ 
                    ### RequestBody
                    ---
                    `albumCode`: 사진을 업로드할 앨범의 코드 \n
                    `fileInfos`: 업로드할 파일 정보 목록 (파일명, 크기, Content-Type) \n
                    
                    ### 로직 상세
                    ---
                    1. 앨범의 존재 및 만료 여부 확인
                    2. 앨범의 최대 사진 개수 (`maxPhotoCount`) 초과 여부 확인
                    3. 파일별 크기(6MB), Content-Type(image/jpeg · image/png · image/jpg) 유효성 검증
                    4. 검증 통과 시, DB에 `Photo` 레코드를 `UPLOADING` 상태로 생성
                    5. 클라우드 스토리지 Presigned URL을 발급하여 반환
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Presigned URL 발급이 성공적으로 완료되었습니다."
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "유효성 검증 실패 (최대 개수 초과, 파일 크기/형식 불일치 등)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = """
                                    {
                                      "isSuccess": false,
                                      "code": 400,
                                      "message": "앨범의 최대 사진 개수를 초과합니다."
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "사용자는 해당 앨범의 참가자가 아닙니다.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = """
                        {
                          "isSuccess": false,
                          "code": 403,
                          "message": "사용자는 해당 앨범의 참가자가 아닙니다."
                        }
                        """)
                    )
            )
    })
    CommonResponse<PhotoPresignedUrlResponse> createPresignedUrls(
            @CurrentUser User user,
            @RequestBody @Valid PhotoPresignedUrlRequest request
    );

    @Operation(
            summary = "사진 업로드 결과 보고 API (부분 성공/실패 처리)", // [추가]
            description = """ 
                    ### RequestBody
                    ---
                    `successPhotoIds`: Object Storage 업로드 성공 ID 목록 \n
                    `failurePhotoIds`: Object Storage 업로드 실패 ID 목록 \n
                    
                    ### 로직 상세
                    ---
                    1. **Success IDs 처리**: `Photo` 상태를 `UPLOADING`에서 `PROCESSING`으로 변경 (후처리 대기).
                    2. **Failure IDs 처리**: `Photo` 상태를 `UPLOADING`에서 `FAILED`으로 변경, 앨범의 `currentPhotoCount`를 **롤백** (감소)합니다.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "사진 업로드 결과 보고가 성공적으로 처리되었습니다."
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "보고된 사진들은 반드시 동일한 앨범에 속해야 합니다.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = """
                        {
                          "isSuccess": false,
                          "code": 400,
                          "message": "보고된 사진들은 반드시 동일한 앨범에 속해야 합니다."
                        }
                        """)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "업로드 결과(success/failure) 목록에 중복된 사진 ID가 포함되어 있습니다.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = """
                        {
                          "isSuccess": false,
                          "code": 400,
                          "message": "업로드 결과(success/failure) 목록에 중복된 사진 ID가 포함되어 있습니다."
                        }
                        """)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "사용자와 사진의 소유자가 일치하지 않습니다.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = """
                        {
                          "isSuccess": false,
                          "code": 403,
                          "message": "사용자와 사진의 소유자가 일치하지 않습니다."
                        }
                        """)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "보고된 사진 ID 중 존재하지 않는 ID가 포함되어 있습니다.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = """
                        {
                          "isSuccess": false,
                          "code": 404,
                          "message": "보고된 사진 ID 중 존재하지 않는 ID가 포함되어 있습니다."
                        }
                        """)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "사진 상태 업데이트에 실패했습니다.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = """
                        {
                          "isSuccess": false,
                          "code": 409,
                          "message": "사진 상태 업데이트에 실패했습니다."
                        }
                        """)
                    )
            )
    })
    CommonResponse<Void> reportUploadResult(
            @CurrentUser User user,
            @RequestBody @Valid PhotoUploadReportRequest request
    );

    @Operation(
            summary = "사진 좋아요 생성 API",
            description = """
                    ### PathVariable
                    ---
                    `photoId`: 사진 ID
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "사진에 대한 좋아요 생성이 성공적으로 실행되었습니다."
            )
    })
    CommonResponse<Void> createPhotoLikes(
            @CurrentUser User user,
            @PathVariable Long photoId
    );

    @Operation(
            summary = "사진 좋아요 삭제 API",
            description = """
                    ### PathVariable
                    ---
                    `photoId`: 사진 ID
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "사진에 대한 좋아요 삭제가 성공적으로 실행되었습니다."
            )
    })
    CommonResponse<Void> deletePhotoLikes(
            @CurrentUser User user,
            @PathVariable Long photoId
    );
}
