package com.trustai.userservice.user.exception;


import com.trustai.userservice.user.exception.base.UserValidationException;

public class UserNotFoundException extends UserValidationException {
    public UserNotFoundException() {
        super();
    }

    public UserNotFoundException(String message) {
        super(message);
    }
}
