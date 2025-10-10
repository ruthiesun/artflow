package com.artflow.artflow.dto;

import jakarta.validation.constraints.Email;

public class ResetRequestDto {
    @Email
    private String email;
    
    public ResetRequestDto(String email) {
        this.email = email;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
}
