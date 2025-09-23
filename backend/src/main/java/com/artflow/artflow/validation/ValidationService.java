package com.artflow.artflow.validation;

import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ValidationService {
    
    private final Map<String, ValidationRule> rules;
    
    public ValidationService(Map<String, ValidationRule> rules) {
        this.rules = rules;
    }
    
    public ValidationRule getRule(String fieldName) {
        return rules.get(fieldName);
    }
    
    public boolean validate(String fieldName, String value) {
        ValidationRule rule = rules.get(fieldName);
        if (rule == null) return false;
        
        if (rule.getRegex() != null && !value.matches(rule.getRegex())) {
            return false;
        }
        if (rule.getMinLength() != null && value.length() < rule.getMinLength()) {
            return false;
        }
        if (rule.getMaxLength() != null && value.length() > rule.getMaxLength()) {
            return false;
        }
        return true;
    }
}
