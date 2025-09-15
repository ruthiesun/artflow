package com.artflow.artflow.security.filter;

import com.artflow.artflow.common.AuthConstants;
import com.artflow.artflow.security.exception.UnsupportedAuthException;
import com.artflow.artflow.security.exception.UnverifiedException;
import com.artflow.artflow.security.service.FirebaseService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class FirebaseAuthenticationFilter extends OncePerRequestFilter {
    private final FirebaseService firebaseService;
    
    public FirebaseAuthenticationFilter(FirebaseService firebaseService) {
        this.firebaseService = firebaseService;
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
        
        String authenticationHeader = request.getHeader(AuthConstants.AUTHORIZATION_HEADER);
        
        if (authenticationHeader == null) {
            // Authentication token is not present, let's rely on anonymous authentication (handles login/signup)
            filterChain.doFilter(request, response);
            return;
        }
        
        String jwtToken = stripBearerPrefix(authenticationHeader);
        
        try {
            String id = firebaseService.resolveIdToken(jwtToken);
            
            // Wrap into Spring Security Authentication
            UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(id, null, Collections.emptyList());
            
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            filterChain.doFilter(request, response);
            
        } catch (Exception e) {
            throw new UnverifiedException();
        }
    }
    
    String stripBearerPrefix(String token) {
        
        if (!token.startsWith(AuthConstants.BEARER_TOKEN_PREAMBLE)) {
            throw new UnsupportedAuthException("Unsupported authentication scheme");
        }
        
        return token.substring(7);
    }
}

