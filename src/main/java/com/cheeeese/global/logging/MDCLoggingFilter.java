package com.cheeeese.global.logging;

import com.cheeeese.global.security.CustomUserDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class MDCLoggingFilter extends OncePerRequestFilter {

    private final LogMaskingUtil logMaskingUtil;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain
    ) throws ServletException, IOException {
        try {
            MDC.put("traceId", UUID.randomUUID().toString().substring(0, 8));
            MDC.put("requestUri", request.getRequestURI());
            MDC.put("httpMethod", request.getMethod());

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication != null
                    && authentication.isAuthenticated()
                    && authentication.getPrincipal() instanceof CustomUserDetails userDetails
            ) {
                MDC.put("userKey", logMaskingUtil.userKey(userDetails.getUser().getId()));
            } else {
                MDC.put("userKey", "null");
            }
            chain.doFilter(request, response);
        } finally {
            MDC.clear();
        }
    }
}
