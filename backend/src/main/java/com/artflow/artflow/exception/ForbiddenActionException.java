package com.artflow.artflow.exception;

public class ForbiddenActionException extends NotFoundException {
    public ForbiddenActionException() {
        super("Does this resource exist or not? Who knows, maybe log in (this was a conscious design decision).");
    }
}
