package com.rozgar.backend.common.exception;

import org.springframework.http.HttpStatus;

public class UnauthorizedException extends RozgarException{
    public UnauthorizedException(String message) {
        super(message, "UNAUTHORIZED", HttpStatus.UNAUTHORIZED);
    }

    public UnauthorizedException() {
        this("Authentication required. Please log in.");
    }
}
