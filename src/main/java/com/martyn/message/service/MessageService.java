package com.martyn.message.service;

import com.martyn.message.data.Message;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class MessageService {
    ConcurrentHashMap<String, List<Message>> messageStore = new ConcurrentHashMap<>();
    ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>> idxMap = new ConcurrentHashMap<>();

    public void recvMessage(Message message) throws Exception {
        String topic = Optional.ofNullable(message
        ).orElse(new Message()).getTopicName();
        if (topic == null || topic.length() == 0) {
            throw new Exception("");
        }

        messageStore.computeIfAbsent(topic, t -> new ArrayList<>());
        messageStore.computeIfPresent(topic, (s, messages) -> {
            messages.add(message);
            return messages;
        });
    }

    public Message pollMessage(String topic, String userId) throws Exception {
        if (!messageStore.containsKey(topic)) {
            throw new Exception("there are no such topic");
        }
        List<Message> messageList = messageStore.getOrDefault(topic, new ArrayList<>());
        ConcurrentHashMap<String, Integer> userMap = idxMap.getOrDefault(topic, new ConcurrentHashMap<>());
        int offset = userMap.getOrDefault(userId, 0);
        offset++;
        userMap.put(userId, offset);
        if (messageList.size() > offset) {
            Message retMsg = messageList.get(offset);
            idxMap.put(topic, userMap);
            return retMsg;
        } else {
            userMap.put(userId, 0);
            throw new Exception("offset exceed the message length, reset to zero");
        }

    }
}
