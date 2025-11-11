package com.cheeeese.cheese4cut.presentation.swagger;

import com.cheeeese.cheese4cut.dto.request.Cheese4cutFixedRequest;
import com.cheeeese.cheese4cut.dto.response.Cheese4cutPresignedUrlResponse;
import com.cheeeese.global.common.CommonResponse;
import com.cheeeese.cheese4cut.dto.response.Cheese4cutResponse;
import com.cheeeese.global.util.CurrentUser;
import com.cheeeese.user.domain.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "[치즈네컷]", description = "치즈네컷 관련 API")
public interface Cheese4cutSwagger {

    @Operation(
            summary = "치즈네컷 정보 조회 API",
            description = """
                    ### PathVariable
                    ---
                    `code`: 앨범 코드
                    
                    ### API 설명 (확정 전/후 분기)
                    ---
                    이 API는 앨범의 **치즈네컷 확정 상태**에 따라 응답 형태가 달라지는 다형적(Polymorphic) 응답을 반환합니다.
                    
                    1. **확정 전 (isFinalized: false)**: 좋아요 TOP 4 사진의 원본 URL, 유니크 좋아요 수, 전체 참여자 수를 제공합니다.
                    2. **확정 후 (isFinalized: true)**: 확정된 좋아요 TOP 4 사진의 원본 URL
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "치즈네컷 정보 조회가 성공적으로 실행되었습니다."
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "완료된 사진 부족으로 미리보기 불가",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                        {
                                          "isSuccess": false,
                                          "code": 400,
                                          "message": "치즈네컷 생성을 위한 완료된 사진이 4장 미만입니다."
                                        }
                                        """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "존재하지 않거나 유효하지 않은 앨범 코드",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                        {
                                          "isSuccess": false,
                                          "code": 404,
                                          "message": "존재하지 않거나 유효하지 않은 앨범 코드입니다."
                                        }
                                        """
                            )
                    )
            )
    })
    CommonResponse<Cheese4cutResponse> getCheese4cut(
            @PathVariable @NotBlank String code
    );

    @Operation(
            summary = "치즈네컷 최종 이미지 업로드용 Presigned URL 발급 API",
            description = """ 
                    ### 로직 상세
                    ---
                    1. 사용자 권한 확인 (MAKER 혹은 참여자만 가능)
                    2. **치즈네컷 전용 버킷**에 저장할 Presigned URL을 발급하여 반환
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Presigned URL 발급이 성공적으로 완료되었습니다."
            )
    })
    CommonResponse<Cheese4cutPresignedUrlResponse> createCheese4cutPresignedUrl(
            @CurrentUser User user,
            @PathVariable @NotBlank String code
    );

    @Operation(
            summary = "치즈네컷 수동 확정 API",
            description = """
                    ### PathVariable
                    ---
                    `code`: 앨범 코드
                    
                    ### RequestBody
                    ---
                    `photoIds`: 사용자가 최종 선택한 4장의 사진 ID \n
                    
                    ### 로직 상세
                    ---
                    1. 사용자 권한 확인 (MAKER만 가능).
                    2. 앨범 만료 및 이미 확정 여부 확인.
                    3. 요청된 4장의 사진 ID가 모두 **COMPLETED 상태**이고 해당 앨범에 속하는지 검증.
                    4. `Cheese4cut` 레코드를 생성하고 저장.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "치즈네컷 수동 확정이 성공적으로 완료되었습니다."
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "유효성 검증 실패 (사진 개수/상태 오류)"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "권한 없음 (MAKER가 아님) 또는 만료된 앨범"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "이미 치즈네컷이 확정되었습니다."
            )
    })
    CommonResponse<Void> finalizeCheese4cut(
            @CurrentUser User user,
            @PathVariable @NotBlank String code,
            @RequestBody @Valid Cheese4cutFixedRequest request
    );
}