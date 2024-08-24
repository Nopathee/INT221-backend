package com.example.int221backend.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter
@Setter
@RequiredArgsConstructor
public class ErrorResponse {
    private final ZonedDateTime timestamp;
    private final int status;
    private final String error;
    private final String message;
}
