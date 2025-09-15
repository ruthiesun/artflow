package com.artflow.artflow.security.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("!test")
public class FirebaseServiceImpl implements FirebaseService {
    
    @Override
    public String createCustomToken(Long id) throws FirebaseAuthException {
        return FirebaseAuth.getInstance().createCustomToken(id.toString());
    }
    
    @Override
    public String resolveIdToken(String token) throws FirebaseAuthException {
        return FirebaseAuth.getInstance().verifyIdToken(token, true).getUid();
    }
}

