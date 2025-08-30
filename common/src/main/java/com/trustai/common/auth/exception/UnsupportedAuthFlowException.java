package com.trustai.common.auth.exception;

public class UnsupportedAuthFlowException extends AuthException {
    public UnsupportedAuthFlowException(String flow) {
        super("Unknown auth flow: " + flow);
    }
}
