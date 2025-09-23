package com.artflow.artflow.dto;

import com.artflow.artflow.dto.common.ValidationConstants;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;

public class LoginDto {
	@Email
	private String email;
	
	@Pattern(
		regexp = ValidationConstants.PASSWORD_REGEX,
		message = ValidationConstants.PASSWORD_MESSAGE
	)
	private String password;
	
	public LoginDto(String email, String password) {
		this.email = email;
		this.password = password;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
}
