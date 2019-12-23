package com.martyn.message.data;


import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.annotation.PostConstruct;
import java.sql.Timestamp;
import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Message {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String uuid;

    private String topicName;
    private String message;
    private Timestamp timestamp;

    public Message() {}

    @PostConstruct
    private void init() {
        uuid = UUID.randomUUID().toString();
    }

    public Message(String topicName, String message, Timestamp timestamp) {
        this.topicName = topicName;
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }


    public static class Builder {
        private String topicName;
        private String message;
        private Timestamp timestamp;

        public Builder setTopicName(String topicName) {
            this.topicName = topicName;
            return this;
        }

        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        public Builder setTimestamp(Timestamp timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Message build() {
            return new Message(this.topicName, this.message, this.timestamp);
        }
    }
}
