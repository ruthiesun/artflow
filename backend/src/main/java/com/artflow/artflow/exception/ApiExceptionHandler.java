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
			.body(Map.of("error", ex.getMessage()));
	}
	
	@ExceptionHandler(FirebaseAuthException.class)
	public ResponseEntity<?> handleFirebase(FirebaseAuthException ex) {
		HttpStatus status = HttpStatus.I_AM_A_TEAPOT; //todo
//		switch (ex.getAuthErrorCode()) {
//			case CERTIFICATE_FETCH_FAILED:
//			case CONFIGURATION_NOT_FOUND:
//			case EMAIL_ALREADY_EXISTS:
//			case EMAIL_NOT_FOUND:
//			case EXPIRED_ID_TOKEN:
//			case EXPIRED_SESSION_COOKIE:
//			case INVALID_DYNAMIC_LINK_DOMAIN:
//			case INVALID_HOSTING_LINK_DOMAIN:
//			case INVALID_ID_TOKEN:
//			case INVALID_SESSION_COOKIE:
//			case PHONE_NUMBER_ALREADY_EXISTS:
//			case REVOKED_ID_TOKEN:
//			case REVOKED_SESSION_COOKIE:
//			case TENANT_ID_MISMATCH:
//			case TENANT_NOT_FOUND:
//			case UID_ALREADY_EXISTS:
//			case UNAUTHORIZED_CONTINUE_URL:
//			case USER_NOT_FOUND:
//			default:// USER_DISABLED
//		}
		return ResponseEntity.status(status)
			.body(Map.of("error", ex.getMessage()));
	}
}
