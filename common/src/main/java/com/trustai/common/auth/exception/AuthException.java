package com.trustai.common.auth.exception;

public class AuthException extends RuntimeException{
    public AuthException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public AuthException(String msg) {
        super(msg);
    }

    public AuthException() {
        super("Authentication Error");
    }
}
