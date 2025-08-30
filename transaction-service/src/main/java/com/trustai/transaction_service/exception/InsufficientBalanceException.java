package com.trustai.transaction_service.exception;

/**
 * Thrown to indicate that a user does not have sufficient balance
 * to complete a transaction.
 */
public class InsufficientBalanceException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public InsufficientBalanceException() {
        super("Insufficient balance to complete the transaction.");
    }

    public InsufficientBalanceException(String message) {
        super(message);
    }

    public InsufficientBalanceException(String message, Throwable cause) {
        super(message, cause);
    }

    public InsufficientBalanceException(Throwable cause) {
        super(cause);
    }
}