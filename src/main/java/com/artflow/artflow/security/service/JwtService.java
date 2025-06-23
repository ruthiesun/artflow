package com.artflow.artflow.security.service;

import com.artflow.artflow.security.exception.InvalidTokenException;
import com.artflow.artflow.security.user.AuthUser;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtService {
	private final Algorithm signingAlgorithm;
	
	public JwtService(@Value("${jwt.signing-secret}") String signingSecret) {
		this.signingAlgorithm = Algorithm.HMAC256(signingSecret); // symmetric signature
	}
	
	public AuthUser resolveJwtToken(String token) {
		try {
			JWTVerifier verifier = JWT.require(signingAlgorithm).build();
			DecodedJWT decodedJWT = verifier.verify(token);
			String email = decodedJWT.getSubject();
			return new AuthUser(email);
		} catch (JWTVerificationException exception) {
			throw new InvalidTokenException();
		}
	}
	
	
	public String createJwtToken(AuthUser authUser) {
		long nowMillis = System.currentTimeMillis();
		Date now = new Date(nowMillis);
		long expMillis = nowMillis + 3600000; // 1 hour validity
		Date exp = new Date(expMillis);
		
		return JWT.create()
				.withSubject(authUser.email())
				.withIssuedAt(now)
				.withExpiresAt(exp)
				.sign(signingAlgorithm);
	}
}
