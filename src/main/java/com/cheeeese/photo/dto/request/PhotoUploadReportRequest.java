package com.cheeeese.photo.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.List;

@Builder
@Schema(description = "사진 업로드 결과 보고 요청 (부분 성공/실패 처리)")
public record PhotoUploadReportRequest(
        @NotNull
        @Schema(description = "업로드가 성공적으로 완료된 사진 ID 목록 (UPLOADING -> PROCESSING)", example = "[100, 102]")
        List<Long> successPhotoIds,

        @NotNull
        @Schema(description = "업로드 중 실패하거나 취소된 사진 ID 목록 (UPLOADING -> FAILED & 롤백)", example = "[101, 103]")
        List<Long> failurePhotoIds
) {
}