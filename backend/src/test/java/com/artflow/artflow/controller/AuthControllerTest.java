package com.artflow.artflow.controller;

import com.artflow.artflow.common.UriUtil;
import com.artflow.artflow.dto.LoginDto;
import com.artflow.artflow.dto.SignupDto;
import com.artflow.artflow.dto.common.ValidationConstants;
import com.artflow.artflow.model.User;
import com.artflow.artflow.repository.UserRepository;
import com.artflow.artflow.security.service.JwtService;
import com.artflow.artflow.security.user.AuthUser;
import com.artflow.artflow.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@TestPropertySource(properties = {
	"jwt.auth-secret=test-secret-auth",
	"jwt.verify-secret=test-secret-verify"
})
public class AuthControllerTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private AuthService authService;
	
	@Autowired
	private JwtService jwtService;
	
	String validEmail = "ruthieismakinganapp@gmail.com";
	String validUsername = "test-username_";
	String validPassword = "testPassword1!";
	
	@Test
	public void canSignUp() throws Exception {
		SignupDto signupDto = new SignupDto(validEmail, validUsername, validPassword);
		
		mockMvc.perform(post(UriUtil.getSignupUri())
						.contentType(APPLICATION_JSON)
						.content(objectMapper.writeValueAsBytes(signupDto)))
				.andExpect(status().isOk());
		
		Optional<User> foundUser = userRepository.findByEmailIgnoreCase(signupDto.getEmail());
		assertTrue(foundUser.isPresent());
	}
	
	@Test
	public void cannotSignUpWithExistingEmail() throws Exception {
		userRepository.save(new User(validEmail.toLowerCase(), validUsername, validPassword));
		
		SignupDto signupDto = new SignupDto(validEmail.toLowerCase(), validUsername + "a", validPassword + "a");
		mockMvc.perform(post(UriUtil.getSignupUri())
						.contentType(APPLICATION_JSON)
						.content(objectMapper.writeValueAsBytes(signupDto)))
				.andExpect(status().isConflict());
		
		signupDto = new SignupDto(validEmail.toUpperCase(), validUsername + "a", validPassword + "a");
		mockMvc.perform(post(UriUtil.getSignupUri())
				.contentType(APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(signupDto)))
			.andExpect(status().isConflict());
	}
	
	@Test
	public void cannotSignUpWithExistingUsername() throws Exception {
		userRepository.save(new User(validEmail, validUsername.toLowerCase(), validPassword));
		
		SignupDto signupDto = new SignupDto("a" + validEmail, validUsername.toLowerCase(), validPassword + "a");
		mockMvc.perform(post(UriUtil.getSignupUri())
				.contentType(APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(signupDto)))
			.andExpect(status().isConflict());
		
		signupDto = new SignupDto("a" + validEmail, validUsername.toUpperCase(), validPassword + "a");
		mockMvc.perform(post(UriUtil.getSignupUri())
				.contentType(APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(signupDto)))
			.andExpect(status().isConflict());
	}
	
	@Test
	public void cannotSignUpWithInvalidEmail() throws Exception {
		String invalidEmail = "bademail";
		
		SignupDto signupDto = new SignupDto(invalidEmail, validUsername, validPassword);
		
		mockMvc.perform(post(UriUtil.getSignupUri())
				.contentType(APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(signupDto)))
			.andExpect(status().isBadRequest());
	}
	
	@Test
	public void cannotSignUpWithInvalidUsername() throws Exception {
		String invalidUsername = "bad username";
		SignupDto signupDto = new SignupDto(validEmail, invalidUsername, validPassword);
		mockMvc.perform(post(UriUtil.getSignupUri())
				.contentType(APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(signupDto)))
			.andExpect(status().isBadRequest());
		
		invalidUsername = "1234";
		signupDto = new SignupDto(validEmail, invalidUsername, validPassword);
		mockMvc.perform(post(UriUtil.getSignupUri())
				.contentType(APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(signupDto)))
			.andExpect(status().isBadRequest());
		
		invalidUsername = "a".repeat(ValidationConstants.USERNAME_LENGTH_MAX + 1);
		signupDto = new SignupDto(validEmail, invalidUsername, validPassword);
		mockMvc.perform(post(UriUtil.getSignupUri())
				.contentType(APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(signupDto)))
			.andExpect(status().isBadRequest());
		
		invalidUsername = "a".repeat(ValidationConstants.USERNAME_LENGTH_MIN - 1);
		signupDto = new SignupDto(validEmail, invalidUsername, validPassword);
		mockMvc.perform(post(UriUtil.getSignupUri())
				.contentType(APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(signupDto)))
			.andExpect(status().isBadRequest());
	}
	
	@Test
	public void cannotSignUpWithInvalidPassword() throws Exception {
		String invalidPassword = "P@word1";
		SignupDto signupDto = new SignupDto(validEmail, validUsername, invalidPassword);
		mockMvc.perform(post(UriUtil.getSignupUri())
				.contentType(APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(signupDto)))
			.andExpect(status().isBadRequest());
		
		invalidPassword = "Password1";
		signupDto = new SignupDto(validEmail, validUsername, invalidPassword);
		mockMvc.perform(post(UriUtil.getSignupUri())
				.contentType(APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(signupDto)))
			.andExpect(status().isBadRequest());
		
		invalidPassword = "P@ssword";
		signupDto = new SignupDto(validEmail, validUsername, invalidPassword);
		mockMvc.perform(post(UriUtil.getSignupUri())
				.contentType(APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(signupDto)))
			.andExpect(status().isBadRequest());
		
		invalidPassword = "p@ssword1";
		signupDto = new SignupDto(validEmail, validUsername, invalidPassword);
		mockMvc.perform(post(UriUtil.getSignupUri())
				.contentType(APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(signupDto)))
			.andExpect(status().isBadRequest());
	}
	
	@Test
	public void canLogin() throws Exception {
		authService.register(new SignupDto(validEmail, validUsername, validPassword));
		User user = userRepository.findByEmailIgnoreCase(validEmail).get();
		user.setIsVerified(true);
		LoginDto loginDto = new LoginDto(validEmail, validPassword);
		
		mockMvc.perform(post(UriUtil.getLoginUri())
						.contentType(APPLICATION_JSON)
						.content(objectMapper.writeValueAsBytes(loginDto)))
				.andExpect(status().isOk());
	}
	
	@Test
	public void cannotLoginWhenUnverified() throws Exception {
		authService.register(new SignupDto(validEmail, validUsername, validPassword));
		LoginDto loginDto = new LoginDto(validEmail, validPassword);
		
		mockMvc.perform(post(UriUtil.getLoginUri())
				.contentType(APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(loginDto)))
			.andExpect(status().isForbidden());
	}
	
	@Test
	public void cannotLoginWithNonexistantEmail() throws Exception {
		authService.register(new SignupDto(validEmail, validUsername, validPassword));
		LoginDto loginDto = new LoginDto(validEmail + "a", validPassword);
		
		mockMvc.perform(post(UriUtil.getLoginUri())
						.contentType(APPLICATION_JSON)
						.content(objectMapper.writeValueAsBytes(loginDto)))
				.andExpect(status().isUnauthorized());
	}
	
	@Test
	public void cannotLoginWithWrongPassword() throws Exception {
		authService.register(new SignupDto(validEmail, validUsername, validPassword));
		User user = userRepository.findByEmailIgnoreCase(validEmail).get();
		user.setIsVerified(true);
		LoginDto loginDto = new LoginDto(validEmail, validPassword + "a");
		
		mockMvc.perform(post(UriUtil.getLoginUri())
						.contentType(APPLICATION_JSON)
						.content(objectMapper.writeValueAsBytes(loginDto)))
				.andExpect(status().isUnauthorized());
	}
	
	@Test
	public void canSignUpAndLogin() throws Exception {
		SignupDto signupDto = new SignupDto(validEmail, validUsername, validPassword);
		
		mockMvc.perform(post(UriUtil.getSignupUri())
				.contentType(APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(signupDto)))
			.andExpect(status().isOk());
		
		User user = userRepository.findByEmailIgnoreCase(validEmail).get();
		String verifyToken = jwtService.createVerifyJwtToken(new AuthUser(user.getId()));
		
		mockMvc.perform(get(UriUtil.getVerifyUri())
				.param("token", verifyToken))
			.andExpect(status().isOk())
			.andReturn();
		
		LoginDto loginDto = new LoginDto(validEmail, validPassword);
		
		mockMvc.perform(post(UriUtil.getLoginUri())
				.contentType(APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(loginDto)))
			.andExpect(status().isOk());
	}
}
