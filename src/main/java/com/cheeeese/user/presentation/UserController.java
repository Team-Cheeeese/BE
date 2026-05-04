package com.cheeeese.user.presentation;

import com.cheeeese.global.common.CommonResponse;
import com.cheeeese.global.util.CurrentUser;
import com.cheeeese.user.application.UserService;
import com.cheeeese.user.domain.User;
import com.cheeeese.user.dto.request.UserOnboardingRequest;
import com.cheeeese.user.dto.request.UserProfileRequest;
import com.cheeeese.user.dto.response.UserInfoResponse;
import com.cheeeese.user.dto.response.UserProfileImageResponse;
import com.cheeeese.user.presentation.swagger.UserSwagger;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static com.cheeeese.global.common.code.SuccessCode.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/user")
public class UserController implements UserSwagger {

    private final UserService userService;

    @Override
    @GetMapping("/me")
    public CommonResponse<UserInfoResponse> getUserInfo(@CurrentUser User user) {
        return CommonResponse.success(USER_INFO_FETCH_SUCCESS, userService.getUserInfo(user));
    }
  
    @Override
    @PatchMapping("/me")
    public CommonResponse<Void> updateUserProfile(
            @CurrentUser User user,
            @RequestBody @Valid UserProfileRequest request
    ) {
        userService.updateUserProfile(user, request);
        return CommonResponse.success(USER_PROFILE_UPDATE_SUCCESS);
    }

    @Override
    @GetMapping("/profile-images")
    public CommonResponse<UserProfileImageResponse> getUserProfileImage() {
        return CommonResponse.success(USER_PROFILE_IMAGE_OPT_GET_SUCCESS, userService.getUserProfileImageOpt());
    }

    @Override
    @PostMapping("/onboarding")
    public CommonResponse<Void> saveUserOnboarding(
            @CurrentUser User user,
            @RequestBody @Valid UserOnboardingRequest request
    ) {
        userService.saveUserOnboarding(user, request);
        return CommonResponse.success(USER_ONBOARDING_SUCCESS);
    }
}
