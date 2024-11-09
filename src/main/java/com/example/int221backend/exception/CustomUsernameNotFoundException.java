package com.example.int221backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class CustomUsernameNotFoundException extends UsernameNotFoundException {
    public CustomUsernameNotFoundException(String message){
        super(message);
    }
}
