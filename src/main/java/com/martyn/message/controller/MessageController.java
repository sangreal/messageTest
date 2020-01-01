package com.martyn.message.controller;

import com.martyn.message.data.Message;
import com.martyn.message.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;

@Controller
@RequestMapping("messages")
public class MessageController {
    @Autowired
    private MessageService messageService;


    @RequestMapping(value = "topics/{topic}/users/{userid}/poll", method = RequestMethod.GET)
    public ResponseEntity pollMessage(@PathVariable("topic") String topic, @PathVariable("userid") String userId) {
        return ResponseEntity.ok(messageService.pollMessage(topic, userId));
    }

    @RequestMapping(value = "topics/{topic}/users/{userid}/ack", method = RequestMethod.PUT)
    public ResponseEntity ackMessage(@PathVariable("topic") String topic, @PathVariable("userid") String userId) {
        return ResponseEntity.ok(messageService.ackMessage(topic, userId));
    }

    @RequestMapping(value = "topics/{topic}/register", method = RequestMethod.POST)
    public ResponseEntity registerTopic(@PathVariable("topic") String topic) {
        messageService.registerTopic(topic);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @RequestMapping(value = "topics/{topic}/users/{userId}/subscribe", method = RequestMethod.PUT)
    public ResponseEntity subscribeTopic(@PathVariable("topic") String topic, @PathVariable("userId") String userId) {
        messageService.subscribeTopic(topic, userId);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "topics/{topic}/publish", method = RequestMethod.PUT)
    public ResponseEntity publishTopic(@PathVariable("topic") String topic, @RequestBody @Valid Message message, BindingResult result) {
        if (result.hasErrors()) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result.getAllErrors());
        }
        messageService.publishMessage(topic, message);
        return ResponseEntity.ok().build();
    }
}
