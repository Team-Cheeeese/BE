package com.cheeeese.user.presentation.swagger;

import com.cheeeese.global.common.CommonResponse;
import com.cheeeese.global.util.CurrentUser;
import com.cheeeese.user.domain.User;
import com.cheeeese.user.dto.request.UserAgreementRequest;
import com.cheeeese.user.dto.request.UserProfileImageRequest;
import com.cheeeese.user.dto.request.UserProfileRequest;
import com.cheeeese.user.dto.response.UserInfoResponse;
import com.cheeeese.user.dto.response.UserProfileImageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "[사용자]", description = "사용자 관련 API")
public interface UserSwagger {
    @Operation(
            summary = "사용자 기본 정보 조회 API",
            description = "사용자의 프로필 이미지, 이름, 참여 앨범 수, 업로드한 사진 수, 받은 좋아요 수를 조회합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "사용자 정보 조회가 성공적으로 완료되었습니다."
            )
    })
    CommonResponse<UserInfoResponse> getUserInfo(@CurrentUser User user);

  
    @Operation(
            summary = "사용자 이름 수정 API",
            description = """
                          ### RequestBody
                          ---
                          `name`: 사용자 이름 (String)
                          """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "사용자 이름이 성공적으로 수정되었습니다."
            )
    })
    CommonResponse<Void> updateUserName(
            @CurrentUser User user,
            @RequestBody @Valid UserProfileRequest request
    );

    @Operation(
            summary = "프로필 이미지 조회 API",
            description = "사용자가 선택할 수 있는 프로필 이미지를 조회합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "프로필 이미지 목록 조회가 성공적으로 수행되었습니다."
            )
    })
    CommonResponse<UserProfileImageResponse> getUserProfileImage();

    @Operation(
            summary = "사용자 프로필 이미지 수정 API",
            description = """
                    ### RequestBody
                    ---
                    `imageCode`: 프로필 이미지 코드 (String)
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "사용자 프로필 이미지 수정이 성공적으로 수행되었습니다."
            )
    })
    CommonResponse<Void> updateUserProfileImage(
            @CurrentUser User user,
            @RequestBody @Valid UserProfileImageRequest request
    );

    @Operation(
            summary = "사용자 이용 약관 동의 API",
            description = """
                          ### RequestBody
                          ---
                          `isServiceAgreement`: 서비스 이용 약관 동의 (boolean) \n
                          `isUserInfoAgreement`: 사용자 정보 수집 동의 (boolean) \n
                          `isMarketingAgreement`: 마케팅 수신 동의 (boolean) \n
                          `isThirdPartyAgreement`: 제3자 제공 동의 (boolean)
                          """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "사용자 이용 약관 동의가 성공적으로 실행되었습니다."
            )
    })
    CommonResponse<Void> saveUserAgreement(
            @CurrentUser User user,
            @RequestBody @Valid UserAgreementRequest request
    );
}
