package com.cheeeese.auth.presentation;

import com.cheeeese.auth.application.AuthService;
import com.cheeeese.auth.dto.request.AuthReissueRequest;
import com.cheeeese.auth.dto.response.AuthReissueResponse;
import com.cheeeese.auth.dto.response.AuthExchangeResponse;
import com.cheeeese.auth.presentation.swagger.AuthSwagger;
import com.cheeeese.global.common.CommonResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static com.cheeeese.global.common.code.SuccessCode.TOKEN_EXCHANGE_SUCCESS;
import static com.cheeeese.global.common.code.SuccessCode.REISSUE_TOKEN_SUCCESS;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/auth")
public class AuthController implements AuthSwagger {

    private final AuthService authService;

    @Override
    @GetMapping("/exchange")
    public CommonResponse<AuthExchangeResponse> exchangeTempCode(@RequestParam String code) {
        return CommonResponse.success(TOKEN_EXCHANGE_SUCCESS, authService.exchangeTempCode(code));
    }

    @Override
    @PostMapping("/reissue")
    public CommonResponse<AuthReissueResponse> reissueToken(@RequestBody @Valid AuthReissueRequest request) {
        return CommonResponse.success(REISSUE_TOKEN_SUCCESS, authService.reissueToken(request));
    }
}
