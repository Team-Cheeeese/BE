package com.cheeeese.auth.presentation;

import com.cheeeese.auth.application.AuthService;
import com.cheeeese.auth.dto.request.AuthReissueRequest;
import com.cheeeese.auth.dto.response.AuthReissueResponse;
import com.cheeeese.auth.dto.response.TempCodeExchangeResponse;
import com.cheeeese.auth.presentation.swagger.AuthSwagger;
import com.cheeeese.global.common.CommonResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static com.cheeeese.global.common.code.SuccessCode.EXCHANGE_TOKEN_SUCCESS;
import static com.cheeeese.global.common.code.SuccessCode.REISSUE_TOKEN_SUCCESS;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/auth")
public class AuthController implements AuthSwagger {

    private final AuthService authService;

    @Override
    @GetMapping("/exchange")
    public CommonResponse<TempCodeExchangeResponse> exchangeTempCode(@RequestParam String code) {
        return CommonResponse.success(EXCHANGE_TOKEN_SUCCESS, authService.exchangeTempCode(code));
    }

    @PostMapping("/reissue")
    public CommonResponse<AuthReissueResponse> reissueToken(@RequestBody @Valid AuthReissueRequest request) {
        return CommonResponse.success(REISSUE_TOKEN_SUCCESS, authService.reissueToken(request));
    }
}
