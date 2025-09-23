package com.artflow.artflow.dto;

import com.artflow.artflow.dto.common.ValidationConstants;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class SignupDto {
	@Email
	private String email;
	
	@Pattern(
		regexp = ValidationConstants.USERNAME_REGEX,
		message = ValidationConstants.USERNAME_MESSAGE
	)
	private String username;
	
	@Pattern(
		regexp = ValidationConstants.PASSWORD_REGEX,
		message = ValidationConstants.PASSWORD_MESSAGE
	)
	private String password;
	
	public SignupDto(String email, String username, String password) {
		this.email = email;
		this.username = username;
		this.password = password;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
}
