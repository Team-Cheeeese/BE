package com.cheeeese.user.presentation;

import com.cheeeese.global.common.CommonResponse;
import com.cheeeese.global.util.CurrentUser;
import com.cheeeese.user.application.UserService;
import com.cheeeese.user.domain.User;
import com.cheeeese.user.dto.request.UserAgreementRequest;
import com.cheeeese.user.dto.request.UserProfileRequest;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.cheeeese.global.common.code.SuccessCode.USER_AGREEMENT_ACCEPT_SUCCESS;
import static com.cheeeese.global.common.code.SuccessCode.USER_PROFILE_UPDATE_SUCCESS;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/user")
public class UserController {

    private final UserService userService;

    @PostMapping("/me/profile")
    public CommonResponse<Void> updateUserProfile(
            @CurrentUser User user,
            @RequestBody @Valid UserProfileRequest request
    ) {
        userService.updateUserProfile(user, request);
        return CommonResponse.success(USER_PROFILE_UPDATE_SUCCESS);
    }

    @PostMapping("/agreement")
    public CommonResponse<Void> saveUserAgreement(
            @CurrentUser User user,
            @RequestBody @Valid UserAgreementRequest request
    ) {
        userService.saveUserAgreement(user, request);
        return CommonResponse.success(USER_AGREEMENT_ACCEPT_SUCCESS);
    }
}
