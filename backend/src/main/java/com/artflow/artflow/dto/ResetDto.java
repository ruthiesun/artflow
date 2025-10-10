package com.artflow.artflow.dto;

import com.artflow.artflow.validation.ValidByRule;

public class ResetDto {
    @ValidByRule("password")
    private String password;
    
    public ResetDto(String password) {
        this.password = password;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
}
