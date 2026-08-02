package com.cheeeese.global.message;

import com.solapi.sdk.message.model.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class FakeKakaoMessageSender implements KakaoMessageSender {

    private final AtomicInteger sendCallCount = new AtomicInteger();
    private final AtomicInteger sendAllCallCount = new AtomicInteger();
    private final List<Message> sentMessages = new ArrayList<>();

    @Override
    public void send(Message message) {
        sendCallCount.incrementAndGet();
        sentMessages.add(message);
    }

    @Override
    public void sendAll(List<Message> messages) {
        sendAllCallCount.incrementAndGet();
        sentMessages.addAll(messages);
    }

    public int getSendCallCount() {
        return sendCallCount.get();
    }

    public int getSendAllCallCount() {
        return sendAllCallCount.get();
    }

    public int getSentMessageCount() {
        return sentMessages.size();
    }

    public List<Message> getSentMessages() {
        return List.copyOf(sentMessages);
    }
}
