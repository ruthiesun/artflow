package com.artflow.artflow.security.filter;

import com.artflow.artflow.common.AuthConstants;
import com.artflow.artflow.security.exception.UnverifiedException;
import com.artflow.artflow.security.service.FirebaseService;
import com.artflow.artflow.security.user.AuthUser;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class FirebaseAuthenticationFilter extends BearerAuthenticationFilter {
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
            
            UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(new AuthUser(Long.parseLong(id)), null, null);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            throw new UnverifiedException();
        }
    }
    
   
}

