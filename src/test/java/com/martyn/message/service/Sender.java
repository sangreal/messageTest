package com.martyn.message.service;

import com.martyn.message.data.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Component
public class Sender {
    @Autowired
    MessageService messageService;

    public void sendMessage(String topic, String message) {
        Message sendMessage = new Message.Builder()
                .setTopicName(topic)
                .setMessage(message)
                .setTimestamp(Timestamp.valueOf(LocalDateTime.now()))
                .build();
        messageService.publishMessage(sendMessage);
    }
}
