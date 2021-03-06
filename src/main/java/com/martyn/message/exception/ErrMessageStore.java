package com.martyn.message.exception;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

public class ErrMessageStore {

    private static Map<Enum, String> errorMap = new ImmutableMap.Builder<Enum, String>()
            .put(ErrorCode.DUP_MESSAGE, "this is a dup message")
            .put(ErrorCode.NO_SUCH_TOPIC, "there is no such topic")
            .put(ErrorCode.NOT_SUBSCRIBED, "consumer has not subscribe to the topic")
            .put(ErrorCode.OFFSET_INVALID, "The offset is invalid")
            .put(ErrorCode.NO_MESSAGE, "There is no messages in the topic")
            .put(ErrorCode.PERSISTENCE_ERR, "There is error when persisting the data")
            .build();

    public static Map<Enum, String> getMap() {
        return errorMap;
    }
}
