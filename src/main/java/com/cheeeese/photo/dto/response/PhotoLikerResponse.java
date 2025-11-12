package com.cheeeese.photo.dto.response;

import com.cheeeese.album.domain.type.Role;
import lombok.Builder;

import java.util.List;

@Builder
public record PhotoLikerResponse(
        int likeCnt,
        List<PhotoLiker> photoLikers
) {

    @Builder
    public record PhotoLiker(
            String name,
            String profileImageUrl,
            boolean isMe,
            Role role
    ) {}
}
