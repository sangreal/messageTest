package com.martyn.message.data;


import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "messages")
public class Message {
    @Id
    @GeneratedValue
    private long id;

    // pid for producer id
    @NotBlank
    private String pid;

    // sequence number for deduplicate
    @NotNull
    private long sn;

    @NotBlank
    private String topicName;
    @NotBlank
    private String message;

    @NotNull
    private Timestamp timestamp;

    public Message() {}


    public Message(String pid, long sn, String topicName, String message, Timestamp timestamp) {
        this.pid = pid;
        this.sn = sn;
        this.topicName = topicName;
        this.message = message;
        this.timestamp = timestamp;
    }


    public long getSn() {
        return sn;
    }

    public String getPid() {
        return pid;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message1 = (Message) o;
        return sn == message1.sn &&
                pid.equals(message1.pid) &&
                message.equals(message1.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pid, sn, message);
    }

    public static class Builder {
        private String pid;
        private long sn;
        private String topicName;
        private String message;
        private Timestamp timestamp;

        public Builder setPid(String pid) {
            this.pid = pid;
            return this;
        }

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
            return new Message(this.pid, this.sn, this.topicName, this.message, this.timestamp);
        }
    }
}
