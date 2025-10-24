package com.cheeeese.photo.presentation.swagger;

import com.cheeeese.global.common.CommonResponse;
import com.cheeeese.global.util.CurrentUser;
import com.cheeeese.photo.dto.request.PhotoPresignedUrlRequest;
import com.cheeeese.photo.dto.response.PhotoPresignedUrlResponse;
import com.cheeeese.user.domain.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
                    3. 파일별 크기(6MB), Content-Type(image/*) 유효성 검증
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
            )
    })
    CommonResponse<PhotoPresignedUrlResponse> createPresignedUrls(
            @CurrentUser User user,
            @RequestBody @Valid PhotoPresignedUrlRequest request
    );
}
