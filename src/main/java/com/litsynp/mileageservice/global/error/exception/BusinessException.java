package com.litsynp.mileageservice.global.error.exception;

import com.litsynp.mileageservice.global.error.ErrorCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;

    public BusinessException(String msg, ErrorCode errorCode) {
        super(msg);
        this.errorCode = errorCode;
    }

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
