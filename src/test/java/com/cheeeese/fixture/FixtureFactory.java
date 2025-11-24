package com.cheeeese.fixture;

import com.cheeeese.album.domain.Album;
import com.cheeeese.album.domain.UserAlbum;
import com.cheeeese.album.domain.type.Role;
import com.cheeeese.album.infrastructure.mapper.AlbumMapper;
import com.cheeeese.album.infrastructure.mapper.UserAlbumMapper;
import com.cheeeese.oauth2.domain.OAuth2UserInfo;
import com.cheeeese.oauth2.infrastructure.userinfo.KakaoUserInfo;
import com.cheeeese.photo.domain.Photo;
import com.cheeeese.photo.domain.PhotoStatus;
import com.cheeeese.photo.infrastructure.mapper.PhotoMapper;
import com.cheeeese.user.domain.User;
import com.cheeeese.user.infrastructure.mapper.UserMapper;
import com.github.f4b6a3.uuid.UuidCreator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FixtureFactory {

    public static KakaoUserInfo createKakaoUserInfo() {
        Map<String, Object> profile = new HashMap<>();
        profile.put("nickname", "카카오유저");
        profile.put("profile_image_url", "https://example.com/kakao-profile.png");

        Map<String, Object> kakaoAccount = new HashMap<>();
        kakaoAccount.put("email", "kakao_user@test.com");
        kakaoAccount.put("profile", profile);

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("id", 1234567890L);
        attributes.put("kakao_account", kakaoAccount);

        return new KakaoUserInfo(attributes);
    }

    public static User createKakaoUser() {
        KakaoUserInfo kakaoInfo = createKakaoUserInfo();
        return UserMapper.toEntity(kakaoInfo);
    }

    public static Album createAlbum(Long userId) {
        return AlbumMapper.toEntity(
                userId,
                "테스트 앨범",
                "테스트 코드",
                "테스트 이미지",
                4,
                LocalDate.of(2025, 1, 1),
                true,
                LocalDateTime.now().plusDays(7)
        );
    }

    public static Album createAlbumV4(int i) {
        return AlbumMapper.toEntity(
                1L,
                "v4-" + i,
                UUID.randomUUID().toString(),
                "테스트 이미지",
                4,
                LocalDate.of(2025, 1, 1),
                true,
                LocalDateTime.now().plusDays(7)
        );
    }

    public static Album createAlbumV7(int i) {
        return AlbumMapper.toEntity(
                1L,
                "v7-" + i,
                UuidCreator.getTimeOrderedEpoch().toString(),
                "테스트 이미지",
                4,
                LocalDate.of(2025, 1, 1),
                true,
                LocalDateTime.now().plusDays(7)
        );
    }

    public static UserAlbum createHostUserAlbum(User user, Album album) {
        return UserAlbumMapper.toEntity(
                user,
                album,
                Role.MAKER
        );
    }

    public static Photo createPhoto(User user, Album album, LocalDateTime captureTime) {
        return PhotoMapper.toEntity(
                user,
                album,
                LocalDateTime.now()
        );
    }
}
