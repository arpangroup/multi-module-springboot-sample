package com.trustai.investment_service.exception;

import lombok.Getter;

@Getter
public class InvalidRequestException extends RuntimeException{
    public InvalidRequestException(String message) {
        super(message);
    }
}
