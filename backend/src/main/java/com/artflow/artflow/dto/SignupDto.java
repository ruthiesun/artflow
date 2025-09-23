package com.artflow.artflow.dto;

import com.artflow.artflow.validation.ValidByRule;
import com.artflow.artflow.validation.ValidationConfig;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;

public class SignupDto {
	@Email
	private String email;
	@ValidByRule("username")
	private String username;
	@ValidByRule("password")
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
