package com.trustai.userservice.user.exception.base;

public class UserValidationException extends RuntimeException{
    public UserValidationException() {
        super();
    }

    public UserValidationException(String message) {
        super(message);
    }
}
