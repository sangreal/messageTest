package com.martyn.message.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.martyn.message.MainApplication;
import com.martyn.message.data.Message;
import com.martyn.message.service.H2JpaConfig;
import com.martyn.message.service.MessageService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
//@WebMvcTest(MessageController.class)
@AutoConfigureMockMvc
@SpringBootTest(classes = {MainApplication.class, H2JpaConfig.class})
public class MockControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    private MessageService service;


    private JacksonTester<Message> jsonMessage;

    @Before
    public void setup() {
        // Initializes the JacksonTester
        JacksonTester.initFields(this, new ObjectMapper());
    }

    @Test
    public void canRetrieveByIdWhenExists() throws Exception {
        String topic = "test";
//        service.registerTopic(topic);
        Message message = new Message.Builder()
                .setMessage("test")
                .setPid("1")
                .setTopicName(topic)
                .setSn(1L)
                .build();
        this.mockMvc.perform(put
                ("/messages/topics/test/publish").content(jsonMessage.write(message).getJson()).contentType("application/json")).andDo(print()).andExpect(status().isOk());
    }}
