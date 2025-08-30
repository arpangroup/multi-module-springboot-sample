package com.trustai.userservice.user.exception;


import com.trustai.userservice.user.exception.base.UserValidationException;

public class IdNotFoundException extends UserValidationException {
    public IdNotFoundException() {
        super("userId not found");
    }

    public IdNotFoundException(String message) {
        super(message);
    }
}
