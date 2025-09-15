package com.artflow.artflow.controller;

import com.artflow.artflow.common.AuthConstants;
import com.artflow.artflow.common.UriUtil;
import com.artflow.artflow.dto.LoginDto;
import com.artflow.artflow.dto.SignupDto;
import com.artflow.artflow.dto.TokenDto;
import com.artflow.artflow.model.User;
import com.artflow.artflow.repository.UserRepository;
import com.artflow.artflow.security.service.JwtService;
import com.artflow.artflow.security.user.AuthUser;
import com.artflow.artflow.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.auth.FirebaseAuth;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

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
		
		Optional<User> foundUser = userRepository.findByEmail(signupDto.getEmail());
		assertTrue(foundUser.isPresent());
	}
	
	@Test
	public void cannotSignUpWithExistingEmail() throws Exception {
		userRepository.save(new User(validEmail, validUsername, validPassword));
		SignupDto signupDto = new SignupDto(validEmail, validUsername + "a", validPassword + "a");
		
		mockMvc.perform(post(UriUtil.getSignupUri())
						.contentType(APPLICATION_JSON)
						.content(objectMapper.writeValueAsBytes(signupDto)))
				.andExpect(status().isConflict());
	}
	
	@Test
	public void cannotSignUpWithExistingUsername() throws Exception {
		userRepository.save(new User(validEmail, validUsername, validPassword));
		SignupDto signupDto = new SignupDto("a" + validEmail, validUsername, validPassword + "a");
		
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
	}
	
	@Test
	public void cannotSignUpWithInvalidPassword() throws Exception {
		String invalidPassword = "Password1";
		
		SignupDto signupDto = new SignupDto(validEmail, validUsername, invalidPassword);
		
		mockMvc.perform(post(UriUtil.getSignupUri())
				.contentType(APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(signupDto)))
			.andExpect(status().isBadRequest());
	}
	
	@Test
	public void canLogin() throws Exception {
		authService.register(new SignupDto(validEmail, validUsername, validPassword));
		User user = userRepository.findByEmail(validEmail).get();
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
		User user = userRepository.findByEmail(validEmail).get();
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
		
		User user = userRepository.findByEmail(validEmail).get();
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
