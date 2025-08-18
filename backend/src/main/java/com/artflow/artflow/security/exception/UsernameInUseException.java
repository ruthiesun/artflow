package com.artflow.artflow.security.exception;

import com.artflow.artflow.exception.InUseException;

public class UsernameInUseException extends InUseException {
    public UsernameInUseException(String username) {
        super("User with username " + username + " already exists");
    }
}
