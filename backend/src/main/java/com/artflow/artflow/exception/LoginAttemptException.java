package com.artflow.artflow.exception;

public class LoginAttemptException extends RateLimitException {
    public LoginAttemptException(String msg) {
        super(msg);
    }
}
