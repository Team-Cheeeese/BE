package com.cheeeese.global.security;

import com.cheeeese.global.common.code.ErrorCode;
import com.cheeeese.global.exception.BusinessException;
import com.cheeeese.user.domain.User;
import com.cheeeese.user.infrastructure.persistence.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        User user = userRepository.findById(Long.valueOf(userId))
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND)); // TODO: 에러 코드 수정

        return new CustomUserDetails(user);
    }
}
