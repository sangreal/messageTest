package com.martyn.message.service;

import com.martyn.message.data.Message;
import com.martyn.message.exception.MyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class Sender {
    @Autowired
    MessageService messageService;

    String uuid;

    @PostConstruct
    private void init() {
        uuid = UUID.randomUUID().toString();
    }

    protected int sn = 1;

    public void sendMessage(String topic, String message) throws MyException  {
        Message sendMessage = new Message.Builder()
                .setPid(uuid)
                .setSn(sn++)
                .setTopicName(topic)
                .setMessage(message)
                .setTimestamp(Timestamp.valueOf(LocalDateTime.now()))
                .build();
        messageService.publishMessage(topic, sendMessage);
    }
}
