package com.cheeeese.cheese4cut.presentation;

import com.cheeeese.cheese4cut.application.Cheese4cutService;
import com.cheeeese.cheese4cut.dto.response.Cheese4cutResponse;
import com.cheeeese.cheese4cut.presentation.swagger.Cheese4cutSwagger;
import com.cheeeese.global.common.CommonResponse;
import com.cheeeese.global.common.code.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/cheese4cut/{code}")
public class Cheese4cutController implements Cheese4cutSwagger {

    private final Cheese4cutService cheese4cutService;

    @GetMapping
    public CommonResponse<Cheese4cutResponse> getCheese4cut(@PathVariable String code) {
        return CommonResponse.success(SuccessCode.CHEESE4CUT_GET_SUCCESS,
                cheese4cutService.getCheese4cutByAlbumCode(code));
    }
}
