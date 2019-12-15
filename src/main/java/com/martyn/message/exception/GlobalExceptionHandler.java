package com.martyn.message.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {MyException.class})
    protected ResponseEntity handle(RuntimeException ex, WebRequest request) {
        return handleExceptionInternal(ex, ex.toString(), new HttpHeaders(),
                    HttpStatus.SERVICE_UNAVAILABLE, request);

    }
}
