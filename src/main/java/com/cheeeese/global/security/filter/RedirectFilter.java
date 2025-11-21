package com.cheeeese.global.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class RedirectFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String redirect = request.getParameter("redirect");

        if (redirect != null && !redirect.isBlank()) {
            String encodedRedirect = URLEncoder.encode(redirect, StandardCharsets.UTF_8);
            Cookie cookie = new Cookie("REDIRECT_URI", encodedRedirect);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            cookie.setMaxAge(300);
            response.addCookie(cookie);
        }
        filterChain.doFilter(request, response);
    }
}
