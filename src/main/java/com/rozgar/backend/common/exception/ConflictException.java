package com.rozgar.backend.common.exception;

import org.springframework.http.HttpStatus;

public class ConflictException extends RozgarException{
    public ConflictException(String message) {
        super(message, "CONFLICT", HttpStatus.CONFLICT);
    }
}
