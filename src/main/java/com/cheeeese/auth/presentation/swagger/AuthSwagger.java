package com.cheeeese.auth.presentation.swagger;

import com.cheeeese.auth.dto.response.TempCodeExchangeResponse;
import com.cheeeese.global.common.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "[사용자 인증/인가]", description = "사용자 로그아웃, 토큰 재발급 관련 API")
public interface AuthSwagger {
    @Operation(
            summary = "사용자 토큰 및 정보 조회 API",
            description = "발급된 임시 코드를 통해 사용자의 accessToken, refreshToken 및 정보를 반환합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "사용자 토큰 및 정보 조회가 성공적으로 실행되었습니다."
            )
    })
    CommonResponse<TempCodeExchangeResponse> exchangeTempCode(
            @RequestParam String code
    );
}
