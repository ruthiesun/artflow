package com.artflow.artflow.controller;

import com.artflow.artflow.common.UriUtil;
import com.artflow.artflow.dto.TokenDto;
import com.artflow.artflow.dto.LoginDto;
import com.artflow.artflow.dto.SignupDto;
import com.artflow.artflow.service.AuthService;
import com.google.firebase.auth.FirebaseAuthException;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(UriUtil.BASE + UriUtil.AUTH)
public class AuthController {
	private final AuthService authService;
	
	public AuthController(AuthService authService) {
		this.authService = authService;
	}

	@PostMapping(UriUtil.SIGNUP)
	public ResponseEntity<String> register(@Valid @RequestBody SignupDto request) {
		authService.register(request);
		return ResponseEntity.ok("An email with a verification link has been sent to " + request.getEmail() + ".");
	}
	
	@PostMapping(UriUtil.LOGIN)
	public ResponseEntity<TokenDto> login(@RequestBody LoginDto request) throws FirebaseAuthException {
		return ResponseEntity.ok(authService.login(request));
	}
	
	@GetMapping(UriUtil.VERIFY)
	public ResponseEntity<String> verifyUser(@RequestParam("token") String token) {
		authService.verify(token);
		return ResponseEntity.ok("Account has been verified.");
	}
}

