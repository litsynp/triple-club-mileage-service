package com.litsynp.mileageservice.error;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ErrorCode {

    INVALID_INPUT_VALUE(400, "C001", "Invalid input value"),
    MESSAGE_NOT_READABLE(400, "C002", "Message not readable"),
    INVALID_TYPE_VALUE(400, "C003", "Invalid type value"),
    ENTITY_NOT_FOUND(404, "C004", "Entity not found"),
    INNER_ENTITY_NOT_FOUND(422, "C005", "Inner entity not found"),
    DUPLICATE_ENTITY(409, "C006", "Entity already exists"),
    INTERNAL_SERVER_ERROR(500, "C007", "Server Error"),
    METHOD_NOT_ALLOWED(405, "C008", "Method not allowed"),
    HANDLE_ACCESS_DENIED(403, "C009", "Access is Denied"),
    ;

    private final int status;
    private final String code;
    private final String message;
}
