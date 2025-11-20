package com.cheeeese.photo.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Schema(description = "Presigned URL 발급 요청")
public record PhotoPresignedUrlRequest(
        @NotNull
        @Schema(description = "앨범 코드", example = "786ccd09-5f22-4aa9-a32b-f62dd2e94cc8")
        String albumCode,

        @NotNull
        @Schema(description = "업로드할 파일 정보 목록")
        List<FileInfo> fileInfos
) {
    @Builder
    @Schema(description = "개별 파일 정보")
    public record FileInfo(
            @NotBlank
            @Schema(description = "원본 파일명", example = "my_holiday_pic.jpg")
            String fileName,

            @NotNull
            @Schema(description = "촬영 시간 (없을 경우 현재 시간)", example = "2025-02-01T14:30:00")
            LocalDateTime captureTime,

            @Schema(description = "파일 크기 (Byte)", example = "3000000")
            long fileSize,

            @NotBlank
            @Schema(description = "파일 Content-Type", example = "image/jpeg")
            String contentType
    ) {}
}