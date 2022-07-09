package com.litsynp.mileageservice.error.exception;

import com.litsynp.mileageservice.error.ErrorCode;
import java.util.UUID;

public class NotFoundException extends BusinessException {

    public NotFoundException(String resourceName, UUID id) {
        super(resourceName + "not found with id = " + id, ErrorCode.ENTITY_NOT_FOUND);
    }
}
