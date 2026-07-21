package com.cheeeese.global.message;

import com.solapi.sdk.message.exception.SolapiEmptyResponseException;
import com.solapi.sdk.message.exception.SolapiMessageNotReceivedException;
import com.solapi.sdk.message.exception.SolapiUnknownException;
import com.solapi.sdk.message.model.Message;

import java.util.List;

public interface KakaoMessageSender {

    void send(Message message) throws
            SolapiMessageNotReceivedException,
            SolapiEmptyResponseException,
            SolapiUnknownException;

    void sendAll(List<Message> messages) throws
            SolapiMessageNotReceivedException,
            SolapiEmptyResponseException,
            SolapiUnknownException;
}
