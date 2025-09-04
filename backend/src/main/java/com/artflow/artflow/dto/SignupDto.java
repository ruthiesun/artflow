package com.artflow.artflow.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class SignupDto {
	@Email
	private String email;
	
	@Pattern(
		regexp = "^(?=.*[a-z])[a-z0-9_-]+$",
		message = "Username must contain at least one letter and can only include lowercase letters, numbers, hyphens, and underscores"
	)
	@Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
	private String username;
	
	@Pattern(
		regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
		message = "Password must be at least 8 characters and include uppercase, lowercase, number, and special character"
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
