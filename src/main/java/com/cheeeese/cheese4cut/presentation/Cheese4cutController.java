package com.cheeeese.cheese4cut.presentation;

import com.cheeeese.cheese4cut.application.Cheese4cutAiService;
import com.cheeeese.cheese4cut.application.Cheese4cutService;
import com.cheeeese.cheese4cut.dto.request.Cheese4cutFixedRequest;
import com.cheeeese.cheese4cut.dto.response.Cheese4cutAiResponse;
import com.cheeeese.cheese4cut.dto.response.Cheese4cutResponse;
import com.cheeeese.cheese4cut.presentation.swagger.Cheese4cutSwagger;
import com.cheeeese.global.common.CommonResponse;
import com.cheeeese.global.util.CurrentUser;
import com.cheeeese.user.domain.User;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.cheeeese.global.common.code.SuccessCode.*;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/cheese4cut/{code}")
public class Cheese4cutController implements Cheese4cutSwagger {

    private final Cheese4cutService cheese4cutService;
    private final Cheese4cutAiService cheese4cutAiService;

    @Override
    @GetMapping("/preview")
    public CommonResponse<Cheese4cutResponse> getCheese4cut(
            Authentication authentication,
            @PathVariable @NotBlank String code
    ) {
        return CommonResponse.success(CHEESE4CUT_GET_SUCCESS,
                cheese4cutService.getCheese4cutByAlbumCode(authentication, code));
    }

    @Override
    @PostMapping("/fixed")
    public CommonResponse<Void> finalizeCheese4cut(
            @CurrentUser User user,
            @PathVariable @NotBlank String code,
            @RequestBody @Valid Cheese4cutFixedRequest request
    ) {
        cheese4cutService.finalizeCheese4cut(user, code, request);
        return CommonResponse.success(CHEESE4CUT_FINALIZE_SUCCESS);
    }

    @Override
    @PostMapping("/fixed/ai")
    public CommonResponse<Void> finalizeCheese4cutWithAi(
            @CurrentUser User user,
            @PathVariable @NotBlank String code,
            @RequestBody @Valid Cheese4cutFixedRequest request
    ) {
        cheese4cutService.finalizeCheese4cutWithAi(user, code, request);
        return CommonResponse.success(CHEESE4CUT_FINALIZE_SUCCESS); // AI 완료를 기다리지 않고 즉시 응답
    }

    @Override
    @GetMapping("/ai-summary")
    public CommonResponse<Cheese4cutAiResponse> getAiSummary(@PathVariable String code) {
        return CommonResponse.success(CHEESE4CUT_AI_GET_SUCCESS,
                cheese4cutAiService.getAiSummary(code)
        );
    }
}
