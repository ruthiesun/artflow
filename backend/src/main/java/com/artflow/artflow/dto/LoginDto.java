package com.artflow.artflow.dto;

import com.artflow.artflow.validation.ValidByRule;
import com.artflow.artflow.validation.ValidationConfig;
import com.artflow.artflow.validation.ValidationService;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;

public class LoginDto {
	@Email
	private String email;
	
	@ValidByRule("password")
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
