package com.artflow.artflow.validation;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.Map;

@Configuration
public class ValidationConfig {
    @Value("file:../shared/validation.json") // relative path
    private Resource validationResource;
    
    @Bean
    public Map<String, ValidationRule> validationRules() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        TypeReference<Map<String, ValidationRule>> typeRef = new TypeReference<>() {};
        return mapper.readValue(validationResource.getInputStream(), typeRef);
    }
}
