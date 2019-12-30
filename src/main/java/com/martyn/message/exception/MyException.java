package com.martyn.message.exception;

public class MyException extends RuntimeException {
    private String message;

    public MyException(ErrorCode msg) {
        message = ErrMessageStore.getMap().getOrDefault(msg, "");
    }


    @Override
    public String toString() {
        return "Exception : " + message;
    }
}
