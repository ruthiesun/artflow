package com.artflow.artflow.security.authentication;

import com.artflow.artflow.security.user.AuthUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Collections;

public record UserAuthentication(AuthUser authUser) implements Authentication {
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Collections.emptyList();
	}
	
	@Override
	public Object getCredentials() {
		return null;
	}
	
	@Override
	public Object getDetails() {
		return null;
	}
	
	@Override
	public Object getPrincipal() {
		return authUser;
	}
	
	@Override
	public boolean isAuthenticated() {
		// This value is set to true in this example because Authentication is used only to represent
		// an authenticated user and not for transferring authentication details
		return true;
	}
	
	@Override
	public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
		// NoOp
	}
	
	@Override
	public String getName() {
		return null;
	}
}
