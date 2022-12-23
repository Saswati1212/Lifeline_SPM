package com.medicalassistance.core.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidUserRequestException extends RuntimeException {
    public InvalidUserRequestException() {
        super();
    }

    public InvalidUserRequestException(String message) {
        super(message);
    }

    public InvalidUserRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}