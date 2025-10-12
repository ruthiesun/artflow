package com.artflow.artflow.controller;

import com.artflow.artflow.common.UriUtil;
import com.artflow.artflow.dto.ResetDto;
import com.artflow.artflow.dto.ResetRequestDto;
import com.artflow.artflow.dto.TokenDto;
import com.artflow.artflow.dto.LoginDto;
import com.artflow.artflow.dto.SignupDto;
import com.artflow.artflow.exception.LoginAttemptException;
import com.artflow.artflow.security.user.AuthUser;
import com.artflow.artflow.service.AuthService;
import com.google.firebase.auth.FirebaseAuthException;
import io.github.bucket4j.Bucket;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@RestController
@RequestMapping(UriUtil.BASE + UriUtil.AUTH)
public class AuthController {
	private final AuthService authService;
	private final Bucket bucket;
	
	
	public AuthController(AuthService authService) {
		this.authService = authService;
		
		this.bucket = Bucket.builder()
			.addLimit(limit -> limit.capacity(10).refillGreedy(10, Duration.ofMinutes(1)))
			.build();
	}

	@PostMapping(UriUtil.SIGNUP)
	public ResponseEntity<String> register(@Valid @RequestBody SignupDto request) {
		authService.register(request);
		return ResponseEntity.ok("An email with a verification link has been sent to " + request.getEmail() + ".");
	}
	
	@PostMapping(UriUtil.LOGIN)
	public ResponseEntity<TokenDto> login(@Valid @RequestBody LoginDto request) throws FirebaseAuthException, LoginAttemptException {
		if (bucket.tryConsume(1)) {
			return ResponseEntity.ok(authService.login(request));
		} else {
			throw new LoginAttemptException("Global login rate limit has been exceeded.");
		}
	}
	
	@PostMapping(UriUtil.LOGOUT)
	public ResponseEntity<Void> logout(@AuthenticationPrincipal AuthUser user) throws FirebaseAuthException {
		authService.logout(user.id());
		return ResponseEntity.ok().build();
	}
	
	@GetMapping(UriUtil.VERIFY)
	public ResponseEntity<String> verifyUser(@RequestParam("token") String token) {
		authService.verify(token);
		return ResponseEntity.ok("Account has been verified.");
	}
	
	@PostMapping(UriUtil.PASSWORD_RESET_REQUEST)
	public ResponseEntity<String> resetPasswordRequest(@Valid @RequestBody ResetRequestDto resetRequestDto) {
		authService.sendResetEmail(resetRequestDto);
		return ResponseEntity.ok("A reset link will be sent to the provided email, if an account exists.");
	}
	
	@PostMapping(UriUtil.PASSWORD_RESET)
	public ResponseEntity<Void> resetPassword(@RequestParam("token") String token, @Valid @RequestBody ResetDto resetDto) {
		authService.reset(resetDto, token);
		return ResponseEntity.ok().build();
	}
	
}

