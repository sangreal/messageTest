package com.martyn.message.service;

import com.martyn.message.data.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Receiver {
    @Autowired
    MessageService messageService;

    public void subscribe(String topic, String userId) {
        messageService.subscribeTopic(topic, userId);
    }

    public synchronized Message pollMessage(String topic, String userid) {
        Message message = messageService.pollMessage(topic, userid);
        messageService.ackMessage(topic, userid);
        return message;
    }
}
