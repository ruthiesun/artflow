package com.artflow.artflow.service;

import com.artflow.artflow.dto.ResetDto;
import com.artflow.artflow.dto.ResetRequestDto;
import com.artflow.artflow.dto.TokenDto;
import com.artflow.artflow.dto.LoginDto;
import com.artflow.artflow.dto.SignupDto;
//import com.artflow.artflow.email.MailService;
import com.artflow.artflow.model.User;
import com.artflow.artflow.repository.UserRepository;
import com.artflow.artflow.security.exception.EmailInUseException;
import com.artflow.artflow.security.exception.InvalidCredentialsException;
import com.artflow.artflow.security.exception.UnverifiedException;
import com.artflow.artflow.security.exception.UsernameInUseException;
import com.artflow.artflow.security.service.FirebaseService;
import com.artflow.artflow.security.service.JwtService;
import com.artflow.artflow.security.user.AuthUser;
import com.google.firebase.auth.FirebaseAuthException;
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
//	private final MailService mailService;
	private final FirebaseService firebaseService;

	public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService, FirebaseService firebaseService) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtService = jwtService;
//		this.mailService = mailService;
		this.firebaseService = firebaseService;
	}
	
	public void register(SignupDto signupDto) {
		if (userRepository.existsByEmailIgnoreCase(signupDto.getEmail())) {
			throw new EmailInUseException(signupDto.getEmail());
		}
		if (userRepository.existsByUsernameIgnoreCase(signupDto.getUsername())) {
			throw new UsernameInUseException(signupDto.getUsername());
		}
		User user = new User(signupDto.getEmail(), signupDto.getUsername(), passwordEncoder.encode(signupDto.getPassword()));
		userRepository.save(user);
		sendVerificationEmail(user.getEmail(), user.getId());
	}
	
	public TokenDto login(LoginDto request) throws FirebaseAuthException {
		User user = userRepository.findByEmailIgnoreCase(request.getEmail()).orElseThrow(InvalidCredentialsException::new);
		if (!user.getIsVerified()) {
			throw new UnverifiedException();
		}
		if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
			throw new InvalidCredentialsException();
		}
		String token = firebaseService.createCustomToken(user.getId());
		
		return new TokenDto(token, user.getUsername());
	}
	
	public void logout(Long userId) throws FirebaseAuthException {
		firebaseService.logout(userId);
	}
	
	public void verify(String token) {
		AuthUser authUser = jwtService.resolveVerifyJwtToken(token);
		User user = userRepository.findById(authUser.id()).orElseThrow(InvalidCredentialsException::new);
		user.setIsVerified(true);
	}
	
	public void sendResetEmail(ResetRequestDto resetRequestDto) {
		String email = resetRequestDto.getEmail();
		if (userRepository.existsByEmailIgnoreCase(email)) {
			sendPasswordResetEmail(email);
		}
	}
	
	public void reset(ResetDto resetDto, String token) {
		String email = jwtService.resolvePasswordResetJwtToken(token);
		User user = userRepository.findByEmailIgnoreCase(email).orElseThrow(InvalidCredentialsException::new);
		if (!user.getIsVerified()) {
			throw new UnverifiedException();
		}
		user.setPassword(passwordEncoder.encode(resetDto.getPassword()));
	}
	
	private void sendVerificationEmail(String email, Long id) {
		String token = jwtService.createVerifyJwtToken(new AuthUser(id));
		String encodedToken = URLEncoder.encode(token, StandardCharsets.UTF_8);
		String link = "http://localhost:5173/verify" + "?token=" + encodedToken; // todo store url in the config
		
//		mailService.sendSimpleMessage(email, "Verify your Artflow account", "Click the link to verify your account: " + link);
	}
	
	private void sendPasswordResetEmail(String email) {
		String token = jwtService.createPasswordResetJwtToken(email);
		String encodedToken = URLEncoder.encode(token, StandardCharsets.UTF_8);
		String link = "http://localhost:5173/reset" + "?token=" + encodedToken; // todo store url in the config
		
//		mailService.sendSimpleMessage(email, "Reset password", "Click the link to reset your password: " + link);
	}
}

