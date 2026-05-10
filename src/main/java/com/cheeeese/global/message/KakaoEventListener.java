package com.cheeeese.global.message;

import com.cheeeese.album.application.support.AlbumReader;
import com.cheeeese.album.domain.Album;
import com.cheeeese.album.domain.event.AlbumJoinedEvent;
import com.cheeeese.user.application.support.UserReader;
import com.cheeeese.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class KakaoEventListener {

    private final UserReader userReader;
    private final AlbumReader albumReader;
    private final KakaoMessageService kakaoMessageService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleAlbumJoined(AlbumJoinedEvent event) {
        try {
            User user = userReader.getUser(event.userId());
            Album album = albumReader.getAlbum(event.albumId());

            kakaoMessageService.sendAlbumJoinedMessage(
                    user.getPhoneNumber(), album.getTitle(), album.getCode()
            );
        } catch (Exception e) {
            log.error("앨범 입장 알림톡 발송 실패", e);
        }
    }
}
