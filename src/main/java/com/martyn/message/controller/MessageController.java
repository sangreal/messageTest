package com.martyn.message.controller;

import com.martyn.message.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("messages")
public class MessageController {
    @Autowired
    private MessageService messageService;


    @RequestMapping("get/topics/{topic}/users/{userid}")
    public ResponseEntity getMessage(@PathVariable("topic") String topic, @PathVariable("userid") String userId) {

    }
}
