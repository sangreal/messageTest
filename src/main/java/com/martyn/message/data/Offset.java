package com.martyn.message.data;



import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Map;

@Entity
public class Offset {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String topic;
    private String userId;
    private int offset;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }


    public static class Builder {
        private String topic;
        private String userId;
        private int offset;

        public Builder setTopic(String topic) {
            this.topic = topic;
            return this;
        }

        public Builder setUserId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder setOffset(int offset) {
            this.offset = offset;
            return this;
        }

        public Offset build(){
            Offset res = new Offset();
            res.setUserId(userId);
            res.setTopic(topic);
            res.setOffset(offset);
            return res;
        }
    }
}
