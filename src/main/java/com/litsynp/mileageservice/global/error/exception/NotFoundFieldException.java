package com.litsynp.mileageservice.global.error.exception;

import com.litsynp.mileageservice.global.error.ErrorCode;

public class NotFoundFieldException extends BusinessException {

    public NotFoundFieldException(String resourceName, String fieldName, String fieldValue) {
        super(resourceName + " not found with " + fieldName + " = " + fieldValue,
                ErrorCode.INNER_ENTITY_NOT_FOUND);
    }
}
