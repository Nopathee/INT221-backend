package com.example.int221backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class ForBiddenException extends RuntimeException {
    public ForBiddenException(String message) {
        super(message);
    }
}
