package com.rozgar.backend.common.exception;

import org.springframework.http.HttpStatus;

public class BadRequestException extends RozgarException{
    public BadRequestException(String message) {
        super(message, "BAD_REQUEST", HttpStatus.BAD_REQUEST);
    }
}
