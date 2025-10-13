package com.cheeeese.album.presentation.swagger;

import com.cheeeese.album.dto.response.AlbumInvitationResponse;
import com.cheeeese.global.common.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PathVariable;

@Tag(name = "[앨범]", description = "앨범 관련 API")
public interface AlbumSwagger {
    @Operation(
            summary = "앨범 초대장 기본 정보 확인 API (로그인 불필요)",
            description = """
                          ### PathVariable
                          ---
                          `code`: 앨범 접근 코드 (URL의 일부)
                          
                          <br>
                          
                          ### API 설명
                          ---
                          URL/QR을 통해 앨범 코드를 전달 받아 제목, 만료일, 호스트 등 기본 정보를 제공합니다. 로그인 여부와 관계없이 호출 가능
                          """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "초대장 정보 조회가 성공적으로 실행되었습니다."
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "존재하지 않거나 유효하지 않은 앨범 코드입니다."
            )
    })
    CommonResponse<AlbumInvitationResponse> getInvitationInfo(
            @PathVariable String code
    );
}