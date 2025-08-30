package com.trustai.userservice.user.exception;


import com.trustai.userservice.user.exception.base.UserValidationException;

public class DuplicateRecordException extends UserValidationException {
    public DuplicateRecordException() {
        super();
    }

    public DuplicateRecordException(String message) {
        super(message);
    }
}
