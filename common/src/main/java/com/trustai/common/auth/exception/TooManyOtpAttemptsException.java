package com.trustai.common.auth.exception;

public class TooManyOtpAttemptsException extends AuthException{
    private final int lockedForMinutes;

    public TooManyOtpAttemptsException(int lockedForMinutes) {
        super("Too many OTP attempts. Locked for " + lockedForMinutes + " minutes.");
        this.lockedForMinutes = lockedForMinutes;
    }

    public int getLockedForMinutes() {
        return lockedForMinutes;
    }
}
