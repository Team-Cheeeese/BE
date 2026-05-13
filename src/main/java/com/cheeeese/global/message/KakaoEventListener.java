package com.cheeeese.global.message;

import com.cheeeese.album.application.support.AlbumReader;
import com.cheeeese.album.domain.Album;
import com.cheeeese.album.domain.UserAlbum;
import com.cheeeese.album.domain.event.AlbumExpireD1Event;
import com.cheeeese.album.domain.event.AlbumJoinedEvent;
import com.cheeeese.cheese4cut.domain.event.Cheese4cutCreatedEvent;
import com.cheeeese.user.application.support.UserReader;
import com.cheeeese.user.domain.User;
import com.solapi.sdk.message.model.Message;
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

            Message message = kakaoMessageService.createAlbumJoinedMessage(
                    user.getPhoneNumber(), album.getTitle(), album.getCode()
            );
            kakaoMessageService.sendMessage(message);
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

            List<Message> messages = participants.stream()
                    .map(participant -> {
                        User user = participant.getUser();

                        return kakaoMessageService.createCheese4cutCreatedMessage(
                                user.getPhoneNumber(), album.getTitle(), album.getCode()
                        );
                    })
                    .toList();

            kakaoMessageService.sendMessages(messages);
        } catch (Exception e) {
            log.error("치즈네컷 알림톡 발송 실패", e);
        }
    }

    @Async
    @EventListener
    public void handleAlbumExpireD1(AlbumExpireD1Event event) {
        try {
            Album album = albumReader.getAlbum(event.albumId());
            List<UserAlbum> participants = albumReader.getAlbumParticipants(event.albumId());

            List<Message> messages = participants.stream()
                    .map(participant -> {
                        User user = participant.getUser();

                        return kakaoMessageService.createAlbumExpireD1Message(
                                user.getPhoneNumber(), album.getTitle(), album.getCode()
                        );
                    })
                    .toList();

            kakaoMessageService.sendMessages(messages);
        } catch (Exception e) {
            log.error("앨범 만료 D-1 알림톡 발송 실패", e);
        }
    }
}
