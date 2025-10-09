package com.cheeeese.global.security.jwt;

import com.cheeeese.auth.application.TokenBlacklistService;
import com.cheeeese.global.security.CustomUserDetailService;
import com.cheeeese.global.security.handler.TokenBlacklistHandler;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final CustomUserDetailService customUserDetailService;
    private final TokenBlacklistService tokenBlacklistService;
    private final TokenBlacklistHandler tokenBlacklistHandler;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String token = jwtProvider.resolveToken(request);

        if (token != null && jwtProvider.validateToken(token)) {
            if (tokenBlacklistService.isBlackListed(token)) {
                tokenBlacklistHandler.handleBlacklistedToken(response);
                return;
            }
            Claims claims = jwtProvider.getClaims(token);
            String userId = claims.getSubject();

            UserDetails userDetails = customUserDetailService.loadUserByUsername(userId);

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities()
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(request, response);
    }
}
