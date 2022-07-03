package com.litsynp.mileageservice.error.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class NotFoundFieldException extends RuntimeException {

    public NotFoundFieldException(String resourceName, String fieldName, String fieldValue) {
        super(resourceName + "not found with " + fieldName + " = " + fieldValue);
    }
}
