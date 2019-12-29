package com.martyn.message.data;


import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.validator.constraints.NotBlank;

import javax.annotation.PostConstruct;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "messages")
public class Message {
    @Id
    @GeneratedValue
    private long id;

    // sequence number for deduplicate
    @NotBlank
    private long sn;

    @NotBlank
    private String topicName;
    @NotBlank
    private String message;

    @NotNull
    private Timestamp timestamp;

    public Message() {}


    public Message(long sn, String topicName, String message, Timestamp timestamp) {
        this.sn = sn;
        this.topicName = topicName;
        this.message = message;
        this.timestamp = timestamp;
    }


    public long getSn() {
        return sn;
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
        private long sn;
        private String topicName;
        private String message;
        private Timestamp timestamp;


        public Builder setSn(long sn) {
            this.sn = sn;
            return this;
        }

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
            return new Message(this.sn, this.topicName, this.message, this.timestamp);
        }
    }
}
