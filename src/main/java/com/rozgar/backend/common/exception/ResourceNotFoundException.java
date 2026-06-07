package com.rozgar.backend.common.exception;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends RozgarException{
    public ResourceNotFoundException(String resource, String identifer){
        super(resource + "not found" + identifer, "RESOURCE_NOT_FOUND", HttpStatus.NOT_FOUND);
    }

    public ResourceNotFoundException(String message) {
        super(message, "RESOURCE_NOT_FOUND", HttpStatus.NOT_FOUND);
    }

}
