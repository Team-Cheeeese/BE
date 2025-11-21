package com.cheeeese.global.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

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
            Cookie cookie = new Cookie("REDIRECT_URI", redirect);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            cookie.setMaxAge(300);
            response.addCookie(cookie);
        }
        filterChain.doFilter(request, response);
    }
}
