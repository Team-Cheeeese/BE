package com.cheeeese.global.security;

import com.cheeeese.global.common.code.ErrorCode;
import com.cheeeese.global.exception.BusinessException;
import com.cheeeese.user.domain.User;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {

    public static User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null ||
            !authentication.isAuthenticated() ||
            authentication instanceof AnonymousAuthenticationToken
        ) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
        return ((CustomUserDetails) authentication.getPrincipal()).getUser();
    }

    public static Long getCurrentUserId() {
        return getCurrentUser().getId();
    }
}
