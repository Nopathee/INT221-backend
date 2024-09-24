package com.example.int221backend.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ItemNotFoundException extends ResponseStatusException {
    public ItemNotFoundException(String msg){
        super(HttpStatus.NOT_FOUND, msg);
    }
}
