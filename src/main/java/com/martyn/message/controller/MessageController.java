package com.martyn.message.controller;

import com.martyn.message.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("messages")
public class MessageController {
    @Autowired
    private MessageService messageService;


    @RequestMapping("topics/{topic}/users/{userid}/poll")
    public ResponseEntity pollMessage(@PathVariable("topic") String topic, @PathVariable("userid") String userId) {
        return ResponseEntity.ok(messageService.pollMessage(topic, userId));
    }

    @RequestMapping(path = "ack/topics/{topic}/users/{userid}", method = RequestMethod.PUT)
    public ResponseEntity ackMessage(@PathVariable("topic") String topic, @PathVariable("userid") String userId) {
        return ResponseEntity.ok(messageService.ackMessage(topic, userId));
    }
}
