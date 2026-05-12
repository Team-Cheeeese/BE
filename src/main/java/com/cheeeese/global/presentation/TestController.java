package com.cheeeese.global.presentation;

import com.cheeeese.album.application.scheduler.AlbumReminderScheduler;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/test")
public class TestController {

    private final AlbumReminderScheduler albumReminderScheduler;

    @PostMapping("/album-reminder")
    public void sendReminder() {
        albumReminderScheduler.notifyExpireD1Albums();
    }
}
