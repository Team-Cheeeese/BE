package com.cheeeese.fixture;

import com.cheeeese.album.domain.Album;
import com.cheeeese.album.domain.UserAlbum;
import com.cheeeese.album.domain.type.Role;
import com.cheeeese.album.infrastructure.mapper.AlbumMapper;
import com.cheeeese.album.infrastructure.mapper.UserAlbumMapper;
import com.cheeeese.oauth2.domain.OAuth2UserInfo;
import com.cheeeese.oauth2.infrastructure.userinfo.KakaoUserInfo;
import com.cheeeese.photo.domain.Photo;
import com.cheeeese.photo.domain.PhotoHistory;
import com.cheeeese.photo.domain.PhotoLikes;
import com.cheeeese.photo.domain.PhotoStatus;
import com.cheeeese.photo.infrastructure.mapper.PhotoHistoryMapper;
import com.cheeeese.photo.infrastructure.mapper.PhotoLikesMapper;
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

    // 인덱스를 받아 고유한 유저 엔티티를 생성하는 메서드
    public static User createUniqueKakaoUser(int index) {
        Map<String, Object> profile = new HashMap<>();
        profile.put("nickname", "유저" + index);
        profile.put("profile_image_url", "https://example.com/profile" + index + ".png");

        Map<String, Object> kakaoAccount = new HashMap<>();
        kakaoAccount.put("email", "user" + index + "@test.com");
        kakaoAccount.put("profile", profile);

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("id", 2000000000L + index);
        attributes.put("kakao_account", kakaoAccount);

        return UserMapper.toEntity(new KakaoUserInfo(attributes));
    }

    public static KakaoUserInfo createKakaoTargetInfo() {
        Map<String, Object> profile = new HashMap<>();
        profile.put("nickname", "카카오타겟");
        profile.put("profile_image_url", "https://example.com/kakao-profile.png");

        Map<String, Object> kakaoAccount = new HashMap<>();
        kakaoAccount.put("email", "kakao_target@test.com");
        kakaoAccount.put("profile", profile);

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("id", 9876543210L);
        attributes.put("kakao_account", kakaoAccount);

        return new KakaoUserInfo(attributes);
    }

    public static User createKakaoUser() {
        KakaoUserInfo kakaoInfo = createKakaoUserInfo();
        return UserMapper.toEntity(kakaoInfo);
    }

    public static User createKakaoTarget() {
        KakaoUserInfo kakaoInfo = createKakaoTargetInfo();
        return UserMapper.toEntity(kakaoInfo);
    }

    public static Album createAlbum(Long userId) {
        String uniqueCode = "ALBUM-" + java.util.UUID.randomUUID().toString().substring(0, 8);
        return AlbumMapper.toEntity(
                userId,
                "테스트 앨범",
                uniqueCode,
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

    public static UserAlbum createGuestUserAlbum(User user, Album album) {
        return UserAlbumMapper.toEntity(
                user,
                album,
                Role.GUEST
        );
    }

    public static Photo createPhoto(User user, Album album, LocalDateTime captureTime) {
        return PhotoMapper.toEntity(
                user,
                album,
                captureTime
        );
    }

    public static Photo createCompletedPhoto(User user, Album album, LocalDateTime now) {
        return Photo.builder()
                .user(user)
                .album(album)
                .imageUrl(null)
                .thumbnailUrl(null)
                .captureTime(now)
                .status(PhotoStatus.COMPLETED)
                .build();
    }

    public static PhotoHistory createPhotoHistory(User user, Photo photo) {
        return PhotoHistoryMapper.toEntity(
                user,
                photo
        );
    }

    public static PhotoLikes createPhotoLikes(User user, Photo photo) {
        return PhotoLikesMapper.toEntity(
                user,
                photo
        );
    }
}
