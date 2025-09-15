package com.artflow.artflow.security.service;

import com.artflow.artflow.security.user.AuthUser;
import com.google.firebase.auth.FirebaseAuthException;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("test")
public class FirebaseServiceTestImpl implements FirebaseService {
    private final JwtService jwtService;
    
    public FirebaseServiceTestImpl(JwtService jwtService) {
        this.jwtService = jwtService;
    }
    
    @Override
    public String createCustomToken(Long id) throws FirebaseAuthException {
        return jwtService.createLoginJwtToken(new AuthUser(id));
    }
    
    @Override
    public String resolveIdToken(String token) throws FirebaseAuthException {
        return jwtService.resolveLoginJwtToken(token).id().toString();
    }
}