package com.artflow.artflow.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RuleValidator implements ConstraintValidator<ValidByRule, String> {
    
    private final ValidationService validationService;
    private String ruleKey;
    
    @Autowired
    public RuleValidator(ValidationService validationService) {
        this.validationService = validationService;
    }
    
    @Override
    public void initialize(ValidByRule annotation) {
        this.ruleKey = annotation.value();
    }
    
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // nulls are handled by @NotNull if needed
        if (value == null) {
            return true;
        }
        
        boolean valid = validationService.validate(ruleKey, value);
        
        if (!valid) {
            // Override message with one from JSON if available
            ValidationRule rule = validationService.getRule(ruleKey);
            if (rule != null && rule.getMessage() != null) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(rule.getMessage())
                    .addConstraintViolation();
            }
        }
        
        return valid;
    }
}
