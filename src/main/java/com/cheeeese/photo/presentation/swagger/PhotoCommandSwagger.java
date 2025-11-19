package com.cheeeese.photo.presentation.swagger;

import com.cheeeese.global.common.CommonResponse;
import com.cheeeese.global.util.CurrentUser;
import com.cheeeese.user.domain.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PathVariable;

@Tag(name = "[사진 - 관리]", description = "사진 관리 (삭제 등)에 대한 API")
public interface PhotoCommandSwagger {
    @Operation(
            summary = "사진 삭제 API",
            description = """
                    ### PathVariable
                    ---
                    `code`: 앨범 코드 (String)
                    `photoId`: 사진 ID (Long)
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "사진 삭제가 성공적으로 실행되었습니다."
            )
    })
    CommonResponse<Void> deletePhoto(
            @CurrentUser User user,
            @PathVariable String code,
            @PathVariable Long photoId
    );
}
