package com.cheeeese.global.message;

import com.cheeeese.album.application.support.AlbumReader;
import com.cheeeese.album.domain.Album;
import com.cheeeese.album.domain.UserAlbum;
import com.cheeeese.album.domain.event.AlbumExpireD1Event;
import com.cheeeese.user.application.support.UserReader;
import com.cheeeese.user.domain.User;
import com.solapi.sdk.message.model.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class KakaoEventListenerTest {

    private KakaoEventListener kakaoEventListener;
    private FakeKakaoMessageSender fakeSender;

    @Mock
    private UserReader userReader;
    @Mock
    private AlbumReader albumReader;
    @Mock
    private KakaoMessageService kakaoMessageService;

    @BeforeEach
    void setUp() {
        fakeSender = new FakeKakaoMessageSender();
        kakaoEventListener = new KakaoEventListener(
                userReader,
                albumReader,
                kakaoMessageService,
                fakeSender
        );
    }

    @Test
    @DisplayName("D-1 알림톡은 실제 Solapi 호출 없이 fake sender로 발송된다.")
    void handleAlbumExpireD1_SendsMessagesWithFakeSender() {
        AlbumExpireD1Event event = AlbumExpireD1Event.of(10L);
        Album album = mock(Album.class);
        UserAlbum participant = mock(UserAlbum.class);
        User user = mock(User.class);
        Message message = new Message();

        given(albumReader.getAlbum(10L)).willReturn(album);
        given(albumReader.getAlbumParticipants(10L)).willReturn(List.of(participant));
        given(participant.getUser()).willReturn(user);
        given(user.getPhoneNumber()).willReturn("01012345678");
        given(album.getTitle()).willReturn("테스트");
        given(album.getCode()).willReturn("album-code");
        given(kakaoMessageService.createAlbumExpireD1Message(
                "01012345678",
                "테스트 앨범",
                "album-code"
        )).willReturn(message);

        kakaoEventListener.handleAlbumExpireD1(event);

        assertThat(fakeSender.getSendAllCallCount()).isEqualTo(1);
        assertThat(fakeSender.getSendCallCount()).isZero();
        assertThat(fakeSender.getSentMessages()).containsExactly(message);
    }
}
