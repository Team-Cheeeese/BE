package com.cheeeese.album.dto.response;

import com.cheeeese.album.domain.type.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Builder
@Schema(
        description = "앨범 참여자 공통 정보 구조",
        requiredProperties = {
                "participants"
        }
)
public record AlbumParticipantListResponse(
        @Schema(description = "참가자 목록 (정렬 포함)")
        List<ParticipantInfo> participants
) {
    @Builder
    @Schema(
            description = "참가자 개별 정보",
            requiredProperties = {
                    "userId",
                    "name",
                    "profileImage",
                    "role",
                    "isMe"
            }
    )
    public record ParticipantInfo(
            @Schema(description = "사용자 ID", example = "1")
            Long userId,

            @Schema(description = "이름", example = "우다현")
            String name,

            @Schema(description = "프로필 이미지 URL", example = "https://cdn.cheeeese.com/users/1/profile.jpg")
            String profileImage,

            @Schema(description = "역할 (MAKER/GUEST)", example = "GUEST")
            Role role,

            @Schema(description = "현재 로그인 사용자 여부", example = "true")
            boolean isMe
    ) {}
}
