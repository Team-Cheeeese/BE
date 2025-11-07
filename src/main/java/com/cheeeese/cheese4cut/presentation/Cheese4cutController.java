package com.cheeeese.cheese4cut.presentation;

import com.cheeeese.cheese4cut.application.Cheese4cutService;
import com.cheeeese.cheese4cut.dto.response.Cheese4cutPresignedUrlResponse;
import com.cheeeese.cheese4cut.dto.response.Cheese4cutResponse;
import com.cheeeese.cheese4cut.presentation.swagger.Cheese4cutSwagger;
import com.cheeeese.global.common.CommonResponse;
import com.cheeeese.global.common.code.SuccessCode;
import com.cheeeese.global.util.CurrentUser;
import com.cheeeese.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static com.cheeeese.global.common.code.SuccessCode.PRESIGNED_URL_ISSUE_SUCCESS;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/cheese4cut/{code}")
public class Cheese4cutController implements Cheese4cutSwagger {

    private final Cheese4cutService cheese4cutService;

    @Override
    @GetMapping("/preview")
    public CommonResponse<Cheese4cutResponse> getCheese4cut(@PathVariable String code) {
        return CommonResponse.success(SuccessCode.CHEESE4CUT_GET_SUCCESS,
                cheese4cutService.getCheese4cutByAlbumCode(code));
    }

    @Override
    @PostMapping("/presigned-url")
    public CommonResponse<Cheese4cutPresignedUrlResponse> createCheese4cutPresignedUrl(
            @CurrentUser User user,
            @PathVariable String code
    ) {
        return CommonResponse.success(PRESIGNED_URL_ISSUE_SUCCESS,
                cheese4cutService.createCheese4cutPresignedUrl(user, code));
    }
}
