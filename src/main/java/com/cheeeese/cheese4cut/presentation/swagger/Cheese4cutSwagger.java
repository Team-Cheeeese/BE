package com.cheeeese.cheese4cut.presentation.swagger;

import com.cheeeese.global.common.CommonResponse;
import com.cheeeese.cheese4cut.dto.response.Cheese4cutResponse;
import com.cheeeese.cheese4cut.dto.response.Cheese4cutPreviewResponse;
import com.cheeeese.cheese4cut.dto.response.Cheese4cutFinalResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PathVariable;

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
                    2. **확정 후 (isFinalized: true)**: 최종 프레임 이미지 URL만 제공합니다. (만료 이벤트 또는 수동 확정 완료)
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "치즈네컷 정보 조회가 성공적으로 실행되었습니다.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(oneOf = {Cheese4cutPreviewResponse.class, Cheese4cutFinalResponse.class}),
                            examples = {
                                    @ExampleObject(
                                            name = "Case 1: 확정 전 미리보기 (버튼 활성화)",
                                            description = "완료된 사진이 4장 이상이며, 최종 확정 전 상태.",
                                            value = """
                                                    {
                                                      "isSuccess": true,
                                                      "code": 200,
                                                      "message": "치즈네컷 정보 조회가 성공적으로 실행되었습니다.",
                                                      "result": {
                                                        "isFinalized": false,
                                                        "previewPhotos": [
                                                          {"photoId": 101, "imageUrl": "https://cdn.img/101_orig.jpg"},
                                                          // ... 4 photos
                                                        ],
                                                        "uniqueLikesCount": 5,
                                                        "participant": 6
                                                      }
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "Case 2: 확정 후 최종 이미지",
                                            description = "앨범이 만료되어 치즈네컷이 자동/수동 확정된 상태.",
                                            value = """
                                                    {
                                                      "isSuccess": true,
                                                      "code": 200,
                                                      "message": "치즈네컷 정보 조회가 성공적으로 실행되었습니다.",
                                                      "result": {
                                                        "isFinalized": true,
                                                        "finalFrameImageUrl": "https://cdn.img/cheese4cut/final/1.png"
                                                      }
                                                    }
                                                    """
                                    )
                            }
                    )
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
            @PathVariable String code
    );
}