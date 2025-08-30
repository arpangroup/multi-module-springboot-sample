package com.trustai.userservice.user.exception;


import com.trustai.userservice.user.exception.base.UserValidationException;

public class InvalidRequestException extends UserValidationException {
    public InvalidRequestException(String message) {
        super(message);
    }
}
