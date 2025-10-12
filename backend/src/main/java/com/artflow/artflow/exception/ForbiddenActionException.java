package com.artflow.artflow.exception;

public class ForbiddenActionException extends NotFoundException {
    public ForbiddenActionException() {
        super("Resource not found");
    }
}
