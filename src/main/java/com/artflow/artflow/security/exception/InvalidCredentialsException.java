package com.artflow.artflow.security.exception;

public class InvalidCredentialsException extends UnauthorizedException {
	public InvalidCredentialsException() {
		super("Invalid login credentials");
	}
}
