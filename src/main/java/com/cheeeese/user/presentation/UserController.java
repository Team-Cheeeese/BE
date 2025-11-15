package com.cheeeese.user.presentation;

import com.cheeeese.global.common.CommonResponse;
import com.cheeeese.global.util.CurrentUser;
import com.cheeeese.user.application.UserService;
import com.cheeeese.user.domain.User;
import com.cheeeese.user.dto.request.UserAgreementRequest;
import com.cheeeese.user.dto.request.UserProfileImageRequest;
import com.cheeeese.user.dto.request.UserProfileRequest;
import com.cheeeese.user.dto.response.UserProfileImageResponse;
import com.cheeeese.user.presentation.swagger.UserSwagger;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static com.cheeeese.global.common.code.SuccessCode.USER_AGREEMENT_ACCEPT_SUCCESS;
import static com.cheeeese.global.common.code.SuccessCode.USER_PROFILE_UPDATE_SUCCESS;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/user")
public class UserController implements UserSwagger {

    private final UserService userService;

    @Override
    @PatchMapping("/me/profile")
    public CommonResponse<Void> updateUserProfile(
            @CurrentUser User user,
            @RequestBody @Valid UserProfileRequest request
    ) {
        userService.updateUserName(user, request);
        return CommonResponse.success(USER_PROFILE_UPDATE_SUCCESS);
    }

    @GetMapping("/profile-images")
    public CommonResponse<UserProfileImageResponse> getUserProfileImage() {
        return CommonResponse.success(USER_PROFILE_UPDATE_SUCCESS, userService.getUserProfileImageOpt());
    }

    @PatchMapping("/me/profile-image")
    public CommonResponse<Void> updateUserProfileImage(
            @CurrentUser User user,
            @RequestBody @Valid UserProfileImageRequest request
    ) {
        userService.updateUserProfileImage(user, request);
        return CommonResponse.success(USER_PROFILE_UPDATE_SUCCESS);
    }

    @Override
    @PostMapping("/agreement")
    public CommonResponse<Void> saveUserAgreement(
            @CurrentUser User user,
            @RequestBody @Valid UserAgreementRequest request
    ) {
        userService.saveUserAgreement(user, request);
        return CommonResponse.success(USER_AGREEMENT_ACCEPT_SUCCESS);
    }
}
