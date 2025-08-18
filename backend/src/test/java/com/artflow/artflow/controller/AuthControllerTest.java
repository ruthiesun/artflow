package com.artflow.artflow.controller;

import com.artflow.artflow.common.UriUtil;
import com.artflow.artflow.dto.LoginDto;
import com.artflow.artflow.dto.SignupDto;
import com.artflow.artflow.model.User;
import com.artflow.artflow.repository.UserRepository;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@TestPropertySource(properties = {
		"jwt.signing-secret=test-secret"
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
	
	@Test
	public void canSignUp() throws Exception {
		SignupDto signupDto = new SignupDto("testemail", "testusername", "testpassword");
		
		mockMvc.perform(post(UriUtil.getSignupUri())
						.contentType(APPLICATION_JSON)
						.content(objectMapper.writeValueAsBytes(signupDto)))
				.andExpect(status().isOk());
		
		Optional<User> foundUser = userRepository.findByEmail(signupDto.getEmail());
		assertTrue(foundUser.isPresent());
	}
	
	@Test
	public void cannotSignUpWithExistingEmail() throws Exception {
		String email = "testemail";
		String username = "testusername";
		String password1 = "testpassword1";
		String password2 = "testpassword2";
		
		userRepository.save(new User(email, username, password1));
		SignupDto signupDto = new SignupDto(email, username, password2);
		
		mockMvc.perform(post(UriUtil.getSignupUri())
						.contentType(APPLICATION_JSON)
						.content(objectMapper.writeValueAsBytes(signupDto)))
				.andExpect(status().isConflict());
	}
	
	@Test
	public void cannotSignUpWithExistingUsername() throws Exception {
		String email1 = "testemail1";
		String email2 = "testemail2";
		String username = "testusername";
		String password1 = "testpassword1";
		String password2 = "testpassword2";
		
		userRepository.save(new User(email1, username, password1));
		SignupDto signupDto = new SignupDto(email2, username, password2);
		
		mockMvc.perform(post(UriUtil.getSignupUri())
				.contentType(APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(signupDto)))
			.andExpect(status().isConflict());
	}
	
	@Test
	public void canLogin() throws Exception {
		User user = new User("testemail", "testusername", "testpassword");
		authService.register(new SignupDto(user.getEmail(), user.getUsername(), user.getPassword()));
		LoginDto loginDto = new LoginDto(user.getEmail(), user.getPassword());
		
		mockMvc.perform(post(UriUtil.getLoginUri())
						.contentType(APPLICATION_JSON)
						.content(objectMapper.writeValueAsBytes(loginDto)))
				.andExpect(status().isOk());
	}
	
	@Test
	public void cannotLoginWithNonexistantEmail() throws Exception {
		String email1 = "testemail1";
		String email2 = "testemail2";
		String username = "testusername";
		String password = "testpassword";
		
		User user = new User(email1, username, password);
		authService.register(new SignupDto(user.getEmail(), user.getUsername(), user.getPassword()));
		LoginDto loginDto = new LoginDto(email2, password);
		
		mockMvc.perform(post(UriUtil.getLoginUri())
						.contentType(APPLICATION_JSON)
						.content(objectMapper.writeValueAsBytes(loginDto)))
				.andExpect(status().isUnauthorized());
	}
	
	@Test
	public void cannotLoginWithWrongPassword() throws Exception {
		String email = "testemail";
		String username = "testusername";
		String password1 = "testpassword1";
		String password2 = "testpassword2";
		
		User user = new User(email, username, password1);
		authService.register(new SignupDto(user.getEmail(), user.getUsername(), user.getPassword()));
		LoginDto loginDto = new LoginDto(email, password2);
		
		mockMvc.perform(post(UriUtil.getLoginUri())
						.contentType(APPLICATION_JSON)
						.content(objectMapper.writeValueAsBytes(loginDto)))
				.andExpect(status().isUnauthorized());
	}
}
