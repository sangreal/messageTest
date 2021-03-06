package com.martyn.message.data.repository;

import com.martyn.message.data.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface MessageRepository extends JpaRepository<Message, String> {
    List<Message> findByPidAndSnAndTopicName(String pid, long sn, String topicName);
    List<Message> findByTopicNameOrderByTimestamp(String topic);
}

