package com.martyn.message.exception;

public class MyException extends RuntimeException {
    private String message;

    public MyException(String msg) {
        message = msg;
    }


    @Override
    public String toString() {
        return "Exception : " + message;
    }
}
