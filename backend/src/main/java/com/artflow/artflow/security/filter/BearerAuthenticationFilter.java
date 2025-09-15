package com.artflow.artflow.security.filter;

import com.artflow.artflow.common.AuthConstants;
import com.artflow.artflow.security.exception.UnsupportedAuthException;
import org.springframework.web.filter.OncePerRequestFilter;

public abstract class BearerAuthenticationFilter extends OncePerRequestFilter {
    protected String stripBearerPrefix(String token) {
        
        if (!token.startsWith(AuthConstants.BEARER_TOKEN_PREAMBLE)) {
            throw new UnsupportedAuthException("Unsupported authentication scheme");
        }
        
        return token.substring(7);
    }
}
