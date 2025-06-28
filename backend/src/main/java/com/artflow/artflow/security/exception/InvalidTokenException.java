package com.artflow.artflow.security.exception;

public class InvalidTokenException extends UnauthorizedException {
	public InvalidTokenException() {
		super("Token is invalid");
	}
	
}
