package com.artflow.artflow.controller;

import com.artflow.artflow.common.UriUtil;
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
@RequestMapping(UriUtil.BASE + UriUtil.AUTH)
public class AuthController {
	private final AuthService authService;
	
	public AuthController(AuthService authService) {
		this.authService = authService;
	}

	@PostMapping(UriUtil.SIGNUP)
	public ResponseEntity<TokenDto> register(@RequestBody SignupDto request) {
		return ResponseEntity.ok(authService.register(request));
	}
	
	@PostMapping(UriUtil.LOGIN)
	public ResponseEntity<TokenDto> login(@RequestBody LoginDto request) {
		return ResponseEntity.ok(authService.login(request));
	}
}

