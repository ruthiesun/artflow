package com.artflow.artflow.service;

import com.artflow.artflow.dto.TokenDto;
import com.artflow.artflow.dto.LoginDto;
import com.artflow.artflow.dto.SignupDto;
import com.artflow.artflow.model.User;
import com.artflow.artflow.repository.UserRepository;
import com.artflow.artflow.security.exception.EmailInUseException;
import com.artflow.artflow.security.exception.InvalidCredentialsException;
import com.artflow.artflow.security.service.JwtService;
import com.artflow.artflow.security.user.AuthUser;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class AuthService {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;

	public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtService = jwtService;
	}
	
	public TokenDto register(SignupDto signupDto) {
		if (userRepository.existsByEmail(signupDto.getEmail())) {
			throw new EmailInUseException(signupDto.getEmail());
		}
		User user = new User(signupDto.getEmail(), passwordEncoder.encode(signupDto.getPassword()));
		userRepository.save(user);
		String token = jwtService.createJwtToken(new AuthUser(user.getEmail()));
		return new TokenDto(token);
	}
	
	public TokenDto login(LoginDto request) {
		User user = userRepository.findByEmail(request.getEmail()).orElseThrow(InvalidCredentialsException::new);
		if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
			throw new InvalidCredentialsException();
		}
		String token = jwtService.createJwtToken(new AuthUser(user.getEmail()));
		return new TokenDto(token);
	}
}

