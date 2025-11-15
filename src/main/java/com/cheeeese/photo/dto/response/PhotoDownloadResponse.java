package com.cheeeese.photo.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Schema(
        description = "사진 다운로드 응답 DTO",
        requiredProperties = {
                "downloadFiles"
        }
)
public record PhotoDownloadResponse(
        @Schema(
                description = "다운로드 가능한 사진 파일 정보 목록",
                example = """
                        [
                          {
                            "photoId": 1,
                            "downloadUrl": "https://kr.objectstorage...",
                            "fileName": "IMG_001.jpg",
                            "captureTime": "2025-11-10T13:45:12",
                            "createdAt": "2025-11-10T14:00:00"
                          },
                          {
                            "photoId": 2,
                            "downloadUrl": null,
                            "fileName": "IMG_002.jpg",
                            "captureTime": "2025-11-10T13:50:30",
                            "createdAt": "2025-11-10T14:10:00"
                          }
                        ]
                        """
        )
        List<DownloadFileInfo> downloadFiles
) {
    @Builder
    @Schema(
            description = "다운로드 가능한 사진 파일 정보",
            requiredProperties = {
                    "photoId",
                    "downloadUrl",
                    "fileName",
                    "captureTime",
                    "createdAt"
            }
    )
    public record DownloadFileInfo(
            @Schema(description = "사진 고유 ID", example = "1")
            Long photoId,

            @Schema(description = "클라우드 스토리지에서 다운로드 받을 URL", example = "https://kr.objectstorage...")
            String downloadUrl,

            @Schema(description = "파일 원본 이름", example = "IMG_002.jpg")
            String fileName,

            @Schema(description = "사진 촬영 시각", example = "2025-11-10T13:50:30")
            LocalDateTime captureTime,

            @Schema(description = "사진 업로드 시각", example = "2025-11-10T14:10:00")
            LocalDateTime createdAt
    ) {}
}
