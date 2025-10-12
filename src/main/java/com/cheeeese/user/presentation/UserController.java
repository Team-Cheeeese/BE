package com.cheeeese.user.presentation;

import com.cheeeese.global.common.CommonResponse;
import com.cheeeese.global.util.CurrentUser;
import com.cheeeese.user.application.UserService;
import com.cheeeese.user.domain.User;
import com.cheeeese.user.dto.request.UserOnboardingRequest;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.cheeeese.global.common.code.SuccessCode.USER_ONBOARDING_SUCCESS;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/user")
public class UserController {

    private final UserService userService;

    @PostMapping("/onboarding")
    public CommonResponse<Void> saveUserOnboarding(
            @CurrentUser User user,
            @RequestBody @Valid UserOnboardingRequest request
    ) {
        userService.saveUserOnboarding(user, request);
        return CommonResponse.success(USER_ONBOARDING_SUCCESS);
    }
}
