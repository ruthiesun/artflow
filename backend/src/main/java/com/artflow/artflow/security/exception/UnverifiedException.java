package com.artflow.artflow.security.exception;

public class UnverifiedException extends RuntimeException {
    public UnverifiedException() {
        super("Account not verified");
    }
}
