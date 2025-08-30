package com.trustai.investment_service.exception;

import lombok.Getter;

@Getter
public class AccessDeniedException extends RuntimeException {
    public AccessDeniedException(String message) {
        super(message);
    }
}
