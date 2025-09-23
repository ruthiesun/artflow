package com.artflow.artflow.validation;

public class ValidationRule {
    private String regex;
    private String message;
    private Integer minLength;
    private Integer maxLength;
    
    public String getRegex() {
        return regex;
    }
    
    public void setRegex(String regex) {
        this.regex = regex;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public Integer getMinLength() {
        return minLength;
    }
    
    public void setMinLength(Integer minLength) {
        this.minLength = minLength;
    }
    
    public Integer getMaxLength() {
        return maxLength;
    }
    
    public void setMaxLength(Integer maxLength) {
        this.maxLength = maxLength;
    }
}
