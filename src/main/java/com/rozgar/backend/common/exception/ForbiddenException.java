package com.rozgar.backend.common.exception;

import org.springframework.http.HttpStatus;

public class ForbiddenException extends RozgarException{

    public ForbiddenException(String message){
        super(message, "FORBIDDEN", HttpStatus.FORBIDDEN);
    }

    public ForbiddenException(){
        this("You do not have permission to perform this action");
    }
}
