package com.litsynp.mileageservice.error.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateResourceException extends RuntimeException{

    public DuplicateResourceException(String resourceName) {
        super(resourceName + " already exists with the same information");
    }
}
