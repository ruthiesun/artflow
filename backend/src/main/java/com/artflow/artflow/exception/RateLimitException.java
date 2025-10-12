package com.artflow.artflow.exception;

public class RateLimitException extends Exception {
    public RateLimitException(String msg) {
        super(msg);
    }
}
