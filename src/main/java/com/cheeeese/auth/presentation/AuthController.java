package com.cheeeese.auth.presentation;

import com.cheeeese.auth.application.AuthService;
import com.cheeeese.auth.dto.request.AuthReissueRequest;
import com.cheeeese.auth.dto.response.AuthReissueResponse;
import com.cheeeese.auth.dto.response.AuthExchangeResponse;
import com.cheeeese.auth.presentation.swagger.AuthSwagger;
import com.cheeeese.global.common.CommonResponse;
import com.cheeeese.global.security.jwt.JwtProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static com.cheeeese.global.common.code.SuccessCode.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/auth")
public class AuthController implements AuthSwagger {

    private final JwtProvider jwtProvider;
    private final AuthService authService;

    @Override
    @GetMapping("/exchange")
    public CommonResponse<AuthExchangeResponse> exchangeTempCode(@RequestParam String code) {
        return CommonResponse.success(TOKEN_EXCHANGE_SUCCESS, authService.exchangeTempCode(code));
    }

    @Override
    @PostMapping("/reissue")
    public CommonResponse<AuthReissueResponse> reissueToken(@RequestBody @Valid AuthReissueRequest request) {
        return CommonResponse.success(TOKEN_REISSUE_SUCCESS, authService.reissueToken(request));
    }

    @Override
    @PostMapping("/logout")
    public CommonResponse<Void> logout(HttpServletRequest request) {
        authService.logout(jwtProvider.resolveToken(request));
        return CommonResponse.success(LOGOUT_SUCCESS);
    }
}
