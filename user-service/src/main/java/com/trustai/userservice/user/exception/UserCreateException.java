package com.trustai.userservice.user.exception;


import com.trustai.userservice.user.exception.base.UserValidationException;

public class UserCreateException extends UserValidationException {
    public UserCreateException() {
        super();
    }

    public UserCreateException(String message) {
        super(message);
    }
}
