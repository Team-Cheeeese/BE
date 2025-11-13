package com.cheeeese.photo.dto.response;

import com.cheeeese.album.domain.type.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Builder
@Schema(description = "띱한 사람 목록 API")
public record PhotoLikerResponse(
        @Schema(description = "사진 좋아요 총 개수", example = "12")
        int likeCnt,

        @Schema(
                description = "사진을 좋아한 사용자 목록",
                example = """
                        [
                          {
                            "name": "홍길동",
                            "profileImageUrl": "https://cdn.cheeeese.me/profile/hong.jpg",
                            "isMe": false,
                            "role": "GUEST"
                          },
                          {
                            "name": "정빈",
                            "profileImageUrl": "https://cdn.cheeeese.me/profile/jb.jpg",
                            "isMe": true,
                            "role": "MAKER"
                          }
                        ]
                        """
        )
        List<PhotoLiker> photoLikers
) {

    @Builder
    @Schema(description = "띱한 사용자 정보")
    public record PhotoLiker(
            @Schema(description = "사용자 이름", example = "홍길동")
            String name,

            @Schema(
                    description = "사용자 프로필 이미지 URL",
                    example = "https://cdn.cheeeese.me/profile/hong.jpg"
            )
            String profileImageUrl,

            @Schema(description = "현재 로그인한 사용자 여부", example = "false")
            boolean isMe,

            @Schema(
                    description = "해당 사진이 속한 앨범에서의 역할",
                    example = "GUEST"
            )
            Role role
    ) {}
}
