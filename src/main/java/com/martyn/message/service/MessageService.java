package com.martyn.message.service;

import com.martyn.message.common.ThreadHelper;
import com.martyn.message.data.Message;
import com.martyn.message.data.Offset;
import com.martyn.message.exception.MyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class MessageService {
    @Autowired
    private CustomContext customContext;

    @Autowired
    private ThreadHelper threadHelper;

    @Autowired
    private PersistenceExecutor persistenceExecutor;

    private Map<String, List<Message>> messageStore;
    private Map<String, Map<String, AtomicInteger>> idxMap;

    @PostConstruct
    private void init() {
        messageStore = customContext.getMessageStore();
        idxMap = customContext.getIdxMap();
    }

    public void publishMessage(Message message) throws MyException {
        String topic = Optional.ofNullable(message
        ).orElse(new Message()).getTopicName();
        if (topic == null || topic.length() == 0) {
            throw new MyException("push message error : topic cannot be null");
        }

        messageStore.computeIfAbsent(topic, t -> new CopyOnWriteArrayList<>());
        messageStore.computeIfPresent(topic, (s, messages) -> {
            // CopyOnWriteArrayList will add Reentrant Lock while adding
            messages.add(message);
            return messages;
        });

        // Use sync call to avoid complex error handling
        try {
            persistenceExecutor.persistMessage(message);
        } catch (Exception e) {
            // if any error happens, roll back for memory cache
            // CopyOnWriteArrayList will add Reentrant Lock while removing
            List<Message> messageList = messageStore.get(topic);
            messageList.remove(messageList.size() - 1);
        }
    }

    public Message pollMessage(String topic, String userId) throws MyException {
        if (!idxMap.get(topic).containsKey(userId)) {
            throw new MyException("receiver has not subsribe this topic yet");
        }
        AtomicInteger offset = getOffsetByUserIdTopicName(topic, userId);
        return getMessageByTopicOffset(topic, offset.get());
    }

    public int ackMessage(String topic, String userId) {
        if (!idxMap.get(topic).containsKey(userId)) {
            throw new MyException("receiver has not subsribe this topic yet");
        }


        int newOffset = incrOffset(topic, userId);
        Offset toStore = new Offset.Builder()
                .setTopic(topic)
                .setUserId(userId)
                .setOffset(newOffset)
                .build();

        // Use sync call to avoid complex error handling
        try {
            persistenceExecutor.persistOffset(toStore);
        } catch (Exception e) {
            // rollback offset by the usage of CAS operation
            decrOffset(topic, userId);
        }

        return newOffset;
    }

    public void subscribeTopic(String topic, String userId) {
        idxMap.putIfAbsent(topic, new ConcurrentHashMap<>());
        idxMap.get(topic).putIfAbsent(userId, new AtomicInteger(0));
    }

    public void registerTopic(String topic) {
        messageStore.putIfAbsent(topic, new CopyOnWriteArrayList<>());
    }

    private  AtomicInteger getOffsetByUserIdTopicName(String topic, String userId) {
        Map<String, AtomicInteger> userMap = idxMap.getOrDefault(topic, new ConcurrentHashMap<>());
        return userMap.getOrDefault(userId, new AtomicInteger(0));
    }

    private  int incrOffset(String topic, String userId) {
        Map<String, AtomicInteger> userMap = idxMap.getOrDefault(topic, new ConcurrentHashMap<>());

        return userMap.get(userId).incrementAndGet();
    }

    private void decrOffset(String topic, String userId) {
        Map<String, AtomicInteger> userMap = idxMap.getOrDefault(topic, new ConcurrentHashMap<>());
        userMap.get(userId).decrementAndGet();
    }

    private Message getMessageByTopicOffset(String topic, int offset) throws MyException {
        if (!messageStore.containsKey(topic)) {
            throw new MyException("there are no such topic");
        }
        List<Message> curlist = messageStore.get(topic);
        if (curlist.size() == offset) {
            return new Message();
        } else if (curlist.size() < offset) {
            throw new MyException("offset is larger than current queue length");
        }

        return messageStore.get(topic).get(offset);
    }


}
