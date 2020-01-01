package com.martyn.message.service;

import com.martyn.message.data.Message;
import com.martyn.message.exception.MyException;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Component
public class ErrorSender extends Sender {
    @Override
    public void sendMessage(String topic, String message) throws MyException {
        Message sendMessage = new Message.Builder()
                .setPid(super.uuid)
                .setSn(0)
                .setTopicName(topic)
                .setMessage(message)
                .setTimestamp(Timestamp.valueOf(LocalDateTime.now()))
                .build();
        messageService.publishMessage(topic, sendMessage);
    }
}
