package com.cheeeese.global.message;

import com.solapi.sdk.message.model.Message;
import com.solapi.sdk.message.model.kakao.KakaoOption;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class KakaoMessageService {

    @Value("${solapi.pf-id}")
    private String pfId;

    @Value("${solapi.sender-number}")
    private String senderNumber;

    private final KakaoTemplateProperties kakaoTemplateProperties;

    public Message createAlbumJoinedMessage(
            String to,
            String albumTitle,
            String albumCode
    ) {
        KakaoOption kakaoOption = new KakaoOption();

        kakaoOption.setPfId(pfId);
        kakaoOption.setTemplateId(kakaoTemplateProperties.albumJoined());

        return createKakaoMessage(
                to, albumTitle, albumCode, "kakao_alarm_enter", kakaoOption
        );
    }

    public Message createCheese4cutCreatedMessage(
            String to,
            String albumTitle,
            String albumCode
    ) {
        KakaoOption kakaoOption = new KakaoOption();

        kakaoOption.setPfId(pfId);
        kakaoOption.setTemplateId(kakaoTemplateProperties.cheese4cutCreated());

        return createKakaoMessage(
                to, albumTitle, albumCode, "kakao_alarm_4cut", kakaoOption
        );
    }

    public Message createAlbumExpireD1Message(
            String to,
            String albumTitle,
            String albumCode
    ) {
        KakaoOption kakaoOption = new KakaoOption();

        kakaoOption.setPfId(pfId);
        kakaoOption.setTemplateId(kakaoTemplateProperties.albumExpireD1());

        return createKakaoMessage(
                to, albumTitle, albumCode, "kakao_alarm_close", kakaoOption
        );
    }

    private Message createKakaoMessage(
            String to,
            String albumTitle,
            String albumCode,
            String utmSource,
            KakaoOption kakaoOption
    ) {
        Map<String, String> variables = new HashMap<>();

        variables.put("#{album_title}", albumTitle);
        variables.put(
                "#{album_code}",
                albumCode + "?utm_source=" + utmSource
        );

        kakaoOption.setVariables(variables);

        Message message = new Message();

        message.setFrom(senderNumber);
        message.setTo(to);
        message.setKakaoOptions(kakaoOption);

        return message;
    }
}
