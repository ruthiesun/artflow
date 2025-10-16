package com.artflow.artflow.exception;

import com.artflow.artflow.security.exception.InvalidCredentialsException;
import com.artflow.artflow.security.exception.UnauthorizedException;
import com.artflow.artflow.security.exception.UnverifiedException;
import com.google.firebase.auth.FirebaseAuthException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {
	@ExceptionHandler(InUseException.class)
	public ResponseEntity<?> handleInUseException(InUseException ex) {
		return ResponseEntity.status(HttpStatus.CONFLICT)
				.body(Map.of("error", ex.getMessage()));
	}
	
	@ExceptionHandler(UnauthorizedException.class)
	public ResponseEntity<?> handleUnauthorized(UnauthorizedException ex) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
				.body(Map.of("error", ex.getMessage()));
	}
	
	@ExceptionHandler(NotFoundException.class)
	public ResponseEntity<?> handleNotFound(NotFoundException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(Map.of("error", ex.getMessage()));
	}
	
	@ExceptionHandler(QueryException.class)
	public ResponseEntity<?> handleNotFound(QueryException ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(Map.of("error", ex.getMessage()));
	}
	
	@ExceptionHandler(UnverifiedException.class)
	public ResponseEntity<?> handleForbidden(UnverifiedException ex) {
		return ResponseEntity.status(HttpStatus.FORBIDDEN)
			.body(Map.of("error", ex.getMessage()));
	}
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<?> handleInvalidArg(MethodArgumentNotValidException ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
			.body(Map.of("error", "Invalid inputs"));
	}
	
	@ExceptionHandler(RateLimitException.class)
	public ResponseEntity<?> handleRateLimit(RateLimitException ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
			.body(Map.of("error", ex.getMessage()));
	}
	
	@ExceptionHandler(DemoException.class)
	public ResponseEntity<?> handleDemo(DemoException ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
			.body(Map.of("error", ex.getMessage()));
	}
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<?> handleGeneral(Exception ex) {
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
			.body(Map.of("error","An internal error occurred"));
	}
}
