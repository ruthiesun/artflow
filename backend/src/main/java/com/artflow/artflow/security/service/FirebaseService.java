package com.artflow.artflow.security.service;

import com.google.firebase.auth.FirebaseAuthException;

public interface FirebaseService {
    public String createCustomToken(Long id) throws FirebaseAuthException;
    public String resolveIdToken(String token) throws FirebaseAuthException;
}

