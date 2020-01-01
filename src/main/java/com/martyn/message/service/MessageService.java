package com.martyn.message.service;

import com.martyn.message.common.ThreadHelper;
import com.martyn.message.data.Message;
import com.martyn.message.data.Offset;
import com.martyn.message.exception.ErrorCode;
import com.martyn.message.exception.MyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class MessageService {
    private static final Logger logger = LoggerFactory.getLogger(MessageService.class);

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

    public void publishMessage(String topic, Message message) throws MyException {
        if (topic == null || topic.length() == 0) {
            throw new MyException(ErrorCode.NULL_TOPIC);
        }

        messageStore.computeIfAbsent(topic, t -> new CopyOnWriteArrayList<>());
        messageStore.computeIfPresent(topic, (s, messages) -> {
            // CopyOnWriteArrayList will add Reentrant Lock while adding
            messages.add(message);
            return messages;
        });

        logger.info("message store size : " + messageStore.get(topic).size());
        // Use sync call to avoid complex error handling
        try {
            persistenceExecutor.persistMessage(message);
        } catch (Exception e) {
            // if any error happens, roll back for memory cache
            // CopyOnWriteArrayList will add Reentrant Lock while removing
            List<Message> messageList = messageStore.get(topic);
            messageList.remove(message);
            if (e instanceof MyException) {
                throw e;
            } else {
                throw new MyException(ErrorCode.PERSISTENCE_ERR);
            }
        }
    }

    public Message pollMessage(String topic, String userId) throws MyException {
        if (!idxMap.get(topic).containsKey(userId)) {
            throw new MyException(ErrorCode.NOT_SUBSCRIBED);
        }
        AtomicInteger offset = getOffsetByUserIdTopicName(topic, userId);
        logger.info("the poll offset is : " + offset.get());
        return getMessageByTopicOffset(topic, offset.get());
    }

    public int ackMessage(String topic, String userId) {
        if (!idxMap.get(topic).containsKey(userId)) {
            throw new MyException(ErrorCode.NOT_SUBSCRIBED);
        }

        int newOffset = incrOffset(topic, userId);
        Offset toStore = new Offset.Builder()
                .setTopic(topic)
                .setUserId(userId)
                .setOffset(newOffset)
                .build();

        logger.warn("tostore offset : " + toStore.toString());

        // TODO change to async committing to remote database
        // Use sync call to avoid complex error handling
        try {
            persistenceExecutor.persistOffset(toStore);
        } catch (Exception e) {
            // rollback offset by the usage of CAS operation
            decrOffset(topic, userId);
            if (e instanceof MyException) {
                throw e;
            } else {
                throw new MyException(ErrorCode.PERSISTENCE_ERR);
            }
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
            throw new MyException(ErrorCode.NO_SUCH_TOPIC);
        }
        List<Message> msgList = messageStore.get(topic);
        logger.info("msgList size : " + msgList.size());
        if (msgList.size() == offset) {
            throw new MyException(ErrorCode.NO_MESSAGE);
        } else if (msgList.size() < offset) {
            logger.warn(String.format("the message list size : %d | offset : %d", msgList.size(), offset));
            throw new MyException(ErrorCode.OFFSET_INVALID);
        }

        return messageStore.get(topic).get(offset);
    }


}
