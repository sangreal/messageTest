package com.martyn.message.service;

import com.martyn.message.data.Message;
import com.martyn.message.data.Offset;
import com.martyn.message.data.repository.MessageRepository;
import com.martyn.message.data.repository.OffsetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Component
public class CustomContext {
    private ConcurrentHashMap<String, List<Message>> messageStore = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Map<String, AtomicInteger>> idxMap = new ConcurrentHashMap<>();

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private OffsetRepository offsetRepository;

    @PostConstruct
    private void initData() {
        messageStore = new ConcurrentHashMap<>();
        idxMap = new ConcurrentHashMap<>();

        List<Message> messageList = messageRepository.findAll();
        List<Offset> offsetList = offsetRepository.findAll();

        Set<String> topics = messageList.stream().map(Message::getTopicName).collect(Collectors.toSet());
        for (String topic : topics) {
            List<Message> userMsgList = messageRepository.findByTopicName(topic);
            messageStore.put(topic, userMsgList);
        }

        Set<String> userIds = offsetList.stream().map(Offset::getUserId).collect(Collectors.toSet());
        for (String topic : topics) {
            for (String userId : userIds) {
                List<Offset> curOffset = offsetRepository.findByTopicAndUserId(topic, userId);
                if (curOffset.size() == 1) {
                    Offset tmpOffset = curOffset.get(0);
                    Map<String, AtomicInteger> tmpMap = new ConcurrentHashMap<>();
                    tmpMap.put(tmpOffset.getUserId(), new AtomicInteger(tmpOffset.getOffset()));
                    idxMap.put(tmpOffset.getTopic(), tmpMap);
                }
            }
        }

    }

    public ConcurrentHashMap<String, List<Message>> getMessageStore() {
        return messageStore;
    }

    public ConcurrentHashMap<String, Map<String, AtomicInteger>> getIdxMap() {
        return idxMap;
    }
}
