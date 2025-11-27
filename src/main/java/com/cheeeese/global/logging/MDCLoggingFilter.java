package com.cheeeese.global.logging;

import com.cheeeese.global.security.CustomUserDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
public class MDCLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain
    ) throws ServletException, IOException {
        String traceId = UUID.randomUUID().toString().substring(0, 8);
        MDC.put("traceId", traceId);

        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication != null
                    && authentication.isAuthenticated()
                    && !"anonymousUser".equals(authentication.getPrincipal())
            ) {
                CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
                MDC.put("userId", String.valueOf(userDetails.getUser().getId()));
            } else {
                MDC.put("userId", "null");
            }
            chain.doFilter(request, response);
        } finally {
            MDC.clear();
        }
    }
}
