package com.artflow.artflow.exception;

public class ForbiddenActionException extends NotFoundException {
    public ForbiddenActionException() {
        super("Cannot perform requested action on the resource");
    }
}
