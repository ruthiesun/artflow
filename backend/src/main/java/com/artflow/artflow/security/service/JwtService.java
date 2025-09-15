package com.artflow.artflow.security.service;

import com.artflow.artflow.security.exception.InvalidTokenException;
import com.artflow.artflow.security.user.AuthUser;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtService {
	private static final Logger log = LoggerFactory.getLogger(JwtService.class);
	private final Algorithm authAlgorithm;
	private final Algorithm verifyAlgorithm;
	private final long authExpMillis;
	private final long verifyExpMillis;
	
	public JwtService(@Value("${jwt.auth-secret}") String authSecret, @Value("${jwt.verify-secret}") String verifySecret,
					  @Value("${jwt.auth-exp-millis}") String authExpMillis, @Value("${jwt.verify-exp-millis}") String verifyExpMillis) {
		this.authAlgorithm = Algorithm.HMAC256(authSecret); // symmetric signature
		this.verifyAlgorithm = Algorithm.HMAC256(verifySecret); // symmetric signature
		this.authExpMillis = Long.parseLong(authExpMillis);
		this.verifyExpMillis = Long.parseLong(verifyExpMillis);
	}
	
	public AuthUser resolveLoginJwtToken(String token) {
		return resolveJwtToken(token, authAlgorithm);
	}
	
	public String createLoginJwtToken(AuthUser authUser) {
		return createJwtToken(authUser, authExpMillis, authAlgorithm);
	}
	
	public AuthUser resolveVerifyJwtToken(String token) {
		return resolveJwtToken(token, verifyAlgorithm);
	}
	
	public String createVerifyJwtToken(AuthUser authUser) {
		return createJwtToken(authUser, verifyExpMillis, verifyAlgorithm);
	}
	
	private AuthUser resolveJwtToken(String token, Algorithm algorithm) {
		try {
			JWTVerifier verifier = JWT.require(algorithm).build();
			DecodedJWT decodedJWT = verifier.verify(token);
			Long id = Long.valueOf(decodedJWT.getSubject());
			return new AuthUser(id);
		} catch (JWTVerificationException exception) {
			throw new InvalidTokenException();
		}
	}
	
	private String createJwtToken(AuthUser authUser, long validDurationMillis, Algorithm algorithm) {
		long nowMillis = System.currentTimeMillis();
		Date now = new Date(nowMillis);
		long expMillis = nowMillis + validDurationMillis;
		Date exp = new Date(expMillis);
		
		return JWT.create()
			.withSubject(authUser.id().toString())
			.withIssuedAt(now)
			.withExpiresAt(exp)
			.sign(algorithm);
	}
}
