package com.trustai.common.exceptions;

public class RestCallException extends RuntimeException {
    public RestCallException(String message, Throwable cause) {
        super(message, cause);
    }
}
