package com.cheeeese.global.logging;

import com.cheeeese.global.security.CustomUserDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.UUID;

@Component
public class MDCLoggingFilter extends OncePerRequestFilter {

    @Value("${log.hash.salt}")
    private String salt;

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
                String rawUserId = String.valueOf(userDetails.getUser().getId());
                MDC.put("userId", maskIdentifier(maskIdentifier(rawUserId)));
            } else {
                MDC.put("userId", "null");
            }
            chain.doFilter(request, response);
        } finally {
            MDC.clear();
        }
    }

    private String maskIdentifier(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(
                    (value + salt).getBytes(StandardCharsets.UTF_8)
            );
            return HexFormat.of().formatHex(hash).substring(0, 12);
        } catch (NoSuchAlgorithmException e) {
            return "unknown";
        }
    }
}
