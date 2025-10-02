package com.cheeeese.global.presentation;

import com.cheeeese.global.common.CommonResponse;
import com.cheeeese.global.presentation.swagger.GlobalSwagger;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.cheeeese.global.common.code.SuccessCode.HEALTH_CHECK_SUCCESS;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/global")
public class GlobalController implements GlobalSwagger {

    @Override
    @GetMapping("/health-check")
    public CommonResponse<String> healthCheck() {
        return CommonResponse.success(HEALTH_CHECK_SUCCESS, "OK");
    }
}
