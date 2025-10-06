package com.cheeeese.auth.presentation;

import com.cheeeese.auth.application.AuthService;
import com.cheeeese.auth.dto.response.TempCodeExchangeResponse;
import com.cheeeese.auth.presentation.swagger.AuthSwagger;
import com.cheeeese.global.common.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.cheeeese.global.common.code.SuccessCode.EXCHANGE_TOKEN_SUCCESS;

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
}
