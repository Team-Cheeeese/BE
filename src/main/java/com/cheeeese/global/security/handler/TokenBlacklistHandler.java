package com.cheeeese.global.security.handler;

import com.cheeeese.auth.exception.code.AuthErrorCode;
import com.cheeeese.global.common.CommonResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class TokenBlacklistHandler {

    private final ObjectMapper objectMapper;

    public void handleBlacklistedToken(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");

        CommonResponse<Void> errorResponse = CommonResponse.failure(AuthErrorCode.LOGGED_OUT_TOKEN);

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
