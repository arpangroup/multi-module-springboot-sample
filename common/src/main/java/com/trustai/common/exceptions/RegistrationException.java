package com.trustai.common.exceptions;

import com.trustai.common.auth.exception.AuthException;

public class RegistrationException extends RuntimeException {
    public RegistrationException(String msg) {
        super(msg);
    }
}
