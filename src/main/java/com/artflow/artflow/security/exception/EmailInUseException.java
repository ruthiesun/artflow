package com.artflow.artflow.security.exception;

import com.artflow.artflow.exception.InUseException;

public class EmailInUseException extends InUseException {
	public EmailInUseException(String email) {
		super("User with email " + email + " already exists");
	}
}
