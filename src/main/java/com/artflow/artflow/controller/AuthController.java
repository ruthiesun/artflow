package com.artflow.artflow.controller;

import com.artflow.artflow.dto.TokenDto;
import com.artflow.artflow.dto.LoginDto;
import com.artflow.artflow.dto.SignupDto;
import com.artflow.artflow.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
	private final AuthService authService;
	
	public AuthController(AuthService authService) {
		this.authService = authService;
	}

	@PostMapping("/signup")
	public ResponseEntity<TokenDto> register(@RequestBody SignupDto request) {
		TokenDto test = authService.register(request);
		return ResponseEntity.ok(test);
	}
	
	@PostMapping("/login")
	public ResponseEntity<TokenDto> login(@RequestBody LoginDto request) {
		return ResponseEntity.ok(authService.login(request));
	}
}

