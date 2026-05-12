package com.cheeeese.global.message;

import com.solapi.sdk.SolapiClient;
import com.solapi.sdk.message.exception.SolapiEmptyResponseException;
import com.solapi.sdk.message.exception.SolapiMessageNotReceivedException;
import com.solapi.sdk.message.exception.SolapiUnknownException;
import com.solapi.sdk.message.model.Message;
import com.solapi.sdk.message.service.DefaultMessageService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.solapi.sdk.message.model.kakao.KakaoOption;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class KakaoMessageService {

    @Value("${solapi.api-key}")
    private String apiKey;

    @Value("${solapi.api-secret}")
    private String apiSecret;

    @Value("${solapi.pf-id}")
    private String pfId;

    @Value("${solapi.sender-number}")
    private String senderNumber;

    private final KakaoTemplateProperties kakaoTemplateProperties;

    private DefaultMessageService messageService;

    @PostConstruct
    public void init() {
        this.messageService = SolapiClient.INSTANCE.createInstance(apiKey, apiSecret);
    }

    public void sendAlbumJoinedMessage(
            String to,
            String albumTitle,
            String albumCode
    ) throws SolapiEmptyResponseException, SolapiUnknownException, SolapiMessageNotReceivedException {
        KakaoOption kakaoOption = new KakaoOption();

        kakaoOption.setPfId(pfId);

        kakaoOption.setTemplateId(kakaoTemplateProperties.albumJoined());

        sendKakaoMessage(to, albumTitle, albumCode, kakaoOption);
    }

    public void sendCheese4cutCreatedMessage(
            String to,
            String albumTitle,
            String albumCode
    ) throws SolapiEmptyResponseException, SolapiUnknownException, SolapiMessageNotReceivedException {
        KakaoOption kakaoOption = new KakaoOption();

        kakaoOption.setPfId(pfId);

        kakaoOption.setTemplateId(kakaoTemplateProperties.cheese4cutCreated());

        sendKakaoMessage(to, albumTitle, albumCode, kakaoOption);
    }

    public void sendAlbumExpireD1Message(
            String to,
            String albumTitle,
            String albumCode
    ) throws SolapiMessageNotReceivedException, SolapiEmptyResponseException, SolapiUnknownException {
        KakaoOption kakaoOption = new KakaoOption();

        kakaoOption.setPfId(pfId);

        kakaoOption.setTemplateId(kakaoTemplateProperties.albumExpireD1());

        sendKakaoMessage(to, albumTitle, albumCode, kakaoOption);
    }

    private void sendKakaoMessage(
            String to,
            String albumTitle,
            String albumCode,
            KakaoOption kakaoOption
    ) throws SolapiMessageNotReceivedException, SolapiEmptyResponseException, SolapiUnknownException {
        Map<String, String> variables = new HashMap<>();

        variables.put("#{album_title}", albumTitle);
        variables.put("#{album_code}", albumCode);

        kakaoOption.setVariables(variables);

        Message message = new Message();

        message.setFrom(senderNumber);

        message.setTo(to);

        message.setKakaoOptions(kakaoOption);

        messageService.send(message);
    }
}
