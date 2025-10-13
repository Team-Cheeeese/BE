package com.cheeeese.user.presentation.swagger;

import com.cheeeese.global.common.CommonResponse;
import com.cheeeese.global.util.CurrentUser;
import com.cheeeese.user.domain.User;
import com.cheeeese.user.dto.request.UserAgreementRequest;
import com.cheeeese.user.dto.request.UserProfileRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "[사용자]", description = "사용자 관련 API")
public interface UserSwagger {
    @Operation(
            summary = "사용자 프로필 수정 API",
            description = """
                          ### RequestBody
                          ---
                          `name`: 사용자 이름
                          """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "사용자 프로필이 성공적으로 수정되었습니다."
            )
    })
    CommonResponse<Void> updateUserProfile(
            @CurrentUser User user,
            @RequestBody @Valid UserProfileRequest request
    );

    @Operation(
            summary = "사용자 이용 약관 동의 API",
            description = """
                          ### RequestBody
                          ---
                          `isServiceAgreement`: 서비스 이용 약관 동의 (boolean) \n
                          `isUserInfoAgreement`: 사용자 정보 수집 동의 (boolean) \n
                          `isMarketingAgreement`: 마케팅 수신 동의 (boolean) \n
                          `isThirdPartyAgreement`: 제3자 제공 동의
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
