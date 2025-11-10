package com.cheeeese.photo.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Builder
@Schema(description = "사진 다운로드 presigned url 발급 API")
public record PhotoDownloadRequest(
        @Schema(description = "앨범 코드", example = "1f0b7ea8-fab6-6581-95e3-0720bc07603e")
        String code,

        @Schema(description = "사진 고유 ID", example = "[1, 2, 3]")
        List<Long> photoIds
) {
}
