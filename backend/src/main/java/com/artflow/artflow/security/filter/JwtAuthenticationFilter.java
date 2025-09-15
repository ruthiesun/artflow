package com.artflow.artflow.security.filter;

import com.artflow.artflow.common.AuthConstants;
import com.artflow.artflow.model.User;
import com.artflow.artflow.repository.UserRepository;
import com.artflow.artflow.security.authentication.UserAuthentication;
import com.artflow.artflow.security.exception.UnsupportedAuthException;
import com.artflow.artflow.security.exception.UnverifiedException;
import com.artflow.artflow.security.service.JwtService;
import com.artflow.artflow.security.user.AuthUser;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	private final JwtService jwtService;
	private final UserRepository userRepository;
	
	public JwtAuthenticationFilter(JwtService jwtService, UserRepository userRepository) {
		this.jwtService = jwtService;
		this.userRepository = userRepository;
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
		AuthUser authUser = jwtService.resolveLoginJwtToken(jwtToken);
		User user = userRepository.findByIdWithProjects(authUser.id()).get();
		if (!user.getIsVerified()) {
			throw new UnverifiedException();
		}
		
		UserAuthentication authentication = new UserAuthentication(authUser);
		
		SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
		securityContext.setAuthentication(authentication);
		SecurityContextHolder.setContext(securityContext);
		
		filterChain.doFilter(request, response);
	}
	
	String stripBearerPrefix(String token) {
		
		if (!token.startsWith(AuthConstants.BEARER_TOKEN_PREAMBLE)) {
			throw new UnsupportedAuthException("Unsupported authentication scheme");
		}
		
		return token.substring(7);
	}
}
