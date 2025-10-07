package com.cheeeese.auth.presentation.swagger;

import com.cheeeese.auth.dto.request.AuthReissueRequest;
import com.cheeeese.auth.dto.response.AuthExchangeResponse;
import com.cheeeese.auth.dto.response.AuthReissueResponse;
import com.cheeeese.global.common.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "[사용자 인증/인가]", description = "사용자 로그아웃, 토큰 재발급 관련 API")
public interface AuthSwagger {
    @Operation(
            summary = "사용자 토큰 및 정보 조회 API",
            description = """
                    ### RequestBody
                    ---
                    `code`: 발급된 임시 코드
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "사용자 토큰 및 정보 조회가 성공적으로 실행되었습니다."
            )
    })
    CommonResponse<AuthExchangeResponse> exchangeTempCode(
            @RequestParam String code
    );

    @Operation(
            summary = "token 재발급 API",
            description = """
                    ### RequestBody
                    ---
                    `refreshToken`: 유효한 refreshToken
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "token 재발급이 성공적으로 실행되었습니다."
            )
    })
    CommonResponse<AuthReissueResponse> reissueToken(
            @RequestBody @Valid AuthReissueRequest request
    );
}
