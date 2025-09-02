package com.artflow.artflow.service;

import com.artflow.artflow.common.UriUtil;
import com.artflow.artflow.dto.TokenDto;
import com.artflow.artflow.dto.LoginDto;
import com.artflow.artflow.dto.SignupDto;
import com.artflow.artflow.email.MailService;
import com.artflow.artflow.model.User;
import com.artflow.artflow.repository.UserRepository;
import com.artflow.artflow.security.exception.EmailInUseException;
import com.artflow.artflow.security.exception.InvalidCredentialsException;
import com.artflow.artflow.security.exception.UnverifiedException;
import com.artflow.artflow.security.exception.UsernameInUseException;
import com.artflow.artflow.security.service.JwtService;
import com.artflow.artflow.security.user.AuthUser;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
@Transactional
public class AuthService {
	private static final Logger log = LoggerFactory.getLogger(AuthService.class);
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;
	private final MailService mailService;

	public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService, MailService mailService) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtService = jwtService;
		this.mailService = mailService;
	}
	
	public void register(SignupDto signupDto) {
		if (userRepository.existsByEmail(signupDto.getEmail())) {
			throw new EmailInUseException(signupDto.getEmail());
		}
		if (userRepository.existsByUsername(signupDto.getUsername())) {
			throw new UsernameInUseException(signupDto.getUsername());
		}
		User user = new User(signupDto.getEmail(), signupDto.getUsername(), passwordEncoder.encode(signupDto.getPassword()));
		userRepository.save(user);
		sendVerificationEmail(user.getEmail());
	}
	
	public TokenDto login(LoginDto request) {
		User user = userRepository.findByEmail(request.getEmail()).orElseThrow(InvalidCredentialsException::new);
		if (!user.getIsVerified()) {
			throw new UnverifiedException();
		}
		if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
			throw new InvalidCredentialsException();
		}
		String token = jwtService.createLoginJwtToken(new AuthUser(user.getEmail()));
		return new TokenDto(token, user.getUsername());
	}
	
	public void verify(String token) {
		AuthUser authUser = jwtService.resolveVerifyJwtToken(token);
		User user = userRepository.findByEmail(authUser.email()).orElseThrow(InvalidCredentialsException::new);
		user.setIsVerified(true);
	}
	
	private void sendVerificationEmail(String email) {
		String token = jwtService.createVerifyJwtToken(new AuthUser(email));
		String encodedToken = URLEncoder.encode(token, StandardCharsets.UTF_8);
		String link = "http://localhost:5173/verify" + "?token=" + encodedToken; // todo store url in the config
		
		mailService.sendSimpleMessage(email, "Verify your Artflow account", "Click the link to verify your account: " + link);
	}
}

