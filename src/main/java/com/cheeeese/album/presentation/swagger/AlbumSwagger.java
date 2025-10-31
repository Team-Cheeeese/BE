package com.cheeeese.album.presentation.swagger;

import com.cheeeese.album.dto.request.AlbumCreationRequest;
import com.cheeeese.album.dto.response.AlbumCreationResponse;
import com.cheeeese.album.dto.response.AlbumEnterResponse;
import com.cheeeese.album.dto.response.AlbumInvitationResponse;
import com.cheeeese.global.common.CommonResponse;
import com.cheeeese.global.util.CurrentUser;
import com.cheeeese.album.dto.response.UploadAvailableCountResponse;
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

@Tag(name = "[앨범]", description = "앨범 관련 API")
public interface AlbumSwagger {
    @Operation(
            summary = "앨범 생성 API",
            description = """ 
                    ### RequestBody
                    ---
                    `themeEmoji`: 앨범 썸네일 이모지 (String) \n
                    `title`: 앨범 이름 (String) \n
                    `participant`: 참여자 수 (int) \n
                    `eventDate`: 행사 날짜 (LocalDate) \n
                    `isTermsAgreement`: 앨범 생성 필수 약관 동의 (boolean)
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "앨범 생성이 성공적으로 실행되었습니다."
            )
    })
    CommonResponse<AlbumCreationResponse> createAlbum(
            @CurrentUser User user,
            @RequestBody @Valid AlbumCreationRequest request
    );

    @Operation(
            summary = "앨범 초대장 기본 정보 확인 API (로그인 불필요)",
            description = """
                          ### PathVariable
                          ---
                          `code`: 앨범 접근 코드 (URL의 일부)
                          
                          <br>
                          
                          ### API 설명
                          ---
                          URL/QR을 통해 앨범 코드를 전달 받아 제목, 만료일, 호스트 등 기본 정보를 제공합니다. 로그인 여부와 관계없이 호출 가능
                          """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "초대장 정보 조회가 성공적으로 실행되었습니다."
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "존재하지 않거나 유효하지 않은 앨범 코드",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = """
                                {
                                  "isSuccess": false,
                                  "code": 404,
                                  "message": "존재하지 않거나 유효하지 않은 앨범 코드입니다."
                                }
                                """)
                    )
            )
    })
    CommonResponse<AlbumInvitationResponse> getInvitationInfo(
            @PathVariable String code
    );

    @Operation(
            summary = "앨범 입장 및 정보 확인 API",
            description = """
              ### Path Variable
              ---
              - `code` : 앨범 접근 코드 (초대 URL의 일부)

              <br>

              ### 처리 로직
              ---
              1. **인증 검증** : 로그인 여부 확인
              2. **앨범 검증** : 전달된 코드로 앨범 존재 여부 및 만료 상태 확인
              3. **접근 권한 검증** : 블랙리스트 사용자 여부 확인
              4. **정원 확인** : 신규 참여자일 경우, 최대 인원 초과 여부 검사
              5. **참여 등록** : 첫 입장 시 `GUEST` 역할로 등록하고, 현재 참여 인원 수 증가
              6. **응답 반환** : 참여 상태(`NEW`, `EXISTING`, `RESTORED`) 및 앨범 정보를 포함한 응답 반환
              """

    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "앨범 정보 조회가 성공적으로 실행되었습니다."
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패 (로그인 필요)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = """
                                {
                                  "isSuccess": false,
                                  "code": 401,
                                  "message": "인증이 필요합니다."
                                }
                                """)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "존재하지 않거나 유효하지 않은 앨범 코드",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = """
                                {
                                  "isSuccess": false,
                                  "code": 404,
                                  "message": "존재하지 않거나 유효하지 않은 앨범 코드입니다."
                                }
                                """)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "앨범 만료 또는 최대 참가 인원 초과",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = """
                                {
                                  "isSuccess": false,
                                  "code": 400,
                                  "message": "앨범이 만료되었거나 최대 인원을 초과했습니다."
                                }
                                """)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "블랙리스트 사용자",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = """
                                {
                                  "isSuccess": false,
                                  "code": 403,
                                  "message": "앨범 관리자에 의해 접근이 금지된 사용자입니다."
                                }
                                """)
                    )
            )
    })
    CommonResponse<AlbumEnterResponse> enterAlbum(
            @CurrentUser User user,
            @PathVariable String code
    );

    @Operation(
            summary = "업로드 가능 사진 수 조회 API",
            description = """ 
                    ### PathVariable
                    ---
                    `code`: 앨범 코드
                    
                    ### 로직 상세
                    ---
                    1. 앨범의 존재, 만료 여부 및 사용자 참가 권한 확인
                    2. `maxPhotoCount` - `currentPhotoCount`를 계산하여 반환
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "업로드 가능 사진 수 조회가 성공적으로 완료되었습니다."
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "참여자가 아닌 사용자의 경우",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = """
                                {
                                  "isSuccess": false,
                                  "code": 403,
                                  "message": "사용자가 해당 앨범의 참가자가 아닙니다."
                                }
                                """)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "존재하지 않거나 유효하지 않은 앨범 코드",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = """
                                {
                                  "isSuccess": false,
                                  "code": 404,
                                  "message": "존재하지 않거나 유효하지 않은 앨범 코드입니다."
                                }
                                """)
                    )
            )
    })
    CommonResponse<UploadAvailableCountResponse> getAvailableUploadCount(
            @CurrentUser User user,
            @PathVariable String code
    );
}