package com.cheeeese.global.message;

import com.cheeeese.album.application.support.AlbumReader;
import com.cheeeese.album.domain.Album;
import com.cheeeese.album.domain.UserAlbum;
import com.cheeeese.album.domain.event.AlbumExpireD1Event;
import com.cheeeese.album.domain.event.AlbumJoinedEvent;
import com.cheeeese.cheese4cut.domain.event.Cheese4cutCreatedEvent;
import com.cheeeese.user.application.support.UserReader;
import com.cheeeese.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

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

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleCheese4cutCreated(Cheese4cutCreatedEvent event) {
        try {
            Album album = albumReader.getAlbum(event.albumId());
            List<UserAlbum> participants = albumReader.getAlbumParticipants(event.albumId());

            for (UserAlbum participant : participants) {
                try {
                    User user = participant.getUser();

                    kakaoMessageService.sendCheese4cutCreatedMessage(
                            user.getPhoneNumber(), album.getTitle(), album.getCode()
                    );
                } catch (Exception e) {
                    log.error(
                            "치즈네컷 생성 알림톡 발송 실패. userId={}",
                            participant.getUser().getId(), e
                    );
                }
            }
        } catch (Exception e) {
            log.error("치즈네컷 생성 이벤트 처리 실패", e);
        }
    }

    @Async
    @EventListener
    public void handleAlbumExpireD1(AlbumExpireD1Event event) {
        try {
            Album album = albumReader.getAlbum(event.albumId());
            List<UserAlbum> participants = albumReader.getAlbumParticipants(event.albumId());

            log.info(
                    "앨범 만료 D-1 알림톡 발송 시작. albumId={}, participantCount={}",
                    album.getId(),
                    participants.size()
            );

            for (UserAlbum participant : participants) {
                try {
                    User user = participant.getUser();

                    kakaoMessageService.sendAlbumExpireD1Message(
                            user.getPhoneNumber(), album.getTitle(), album.getCode()
                    );
                } catch (Exception e) {
                    log.error(
                            "앨범 만료 D-1 알림톡 발송 실패. userId={}",
                            participant.getUser().getId(), e
                    );
                }
            }
        } catch (Exception e) {
            log.error("앨범 만료 D-1 이벤트 처리 실패", e);
        }
    }
}
