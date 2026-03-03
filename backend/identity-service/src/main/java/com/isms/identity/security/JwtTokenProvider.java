package com.isms.identity.security;

import java.util.Date;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import com.isms.identity.config.JwtConfig;
import com.isms.identity.entity.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

	private final JwtConfig jwtConfig;

	private SecretKey getSigningKey() {
		return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtConfig.getSecret()));
	}

	public String generateAccessToken(CustomUserDetails userDetails) {
		Date now = new Date();
		Date expiry = new Date(now.getTime() + jwtConfig.getAccessTokenExpiration());
		User user = userDetails.getUser();
		return Jwts.builder().setSubject(user.getEmail()).claim("userId", user.getId().toString()).setIssuedAt(now)
				.setExpiration(expiry).signWith(getSigningKey()).compact();
	}

	public boolean validateToken(String token) {
		try {
			Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public Long getAccessTokenExpiration() {
		return jwtConfig.getAccessTokenExpiration();
	}

	public Long getRefreshTokenExpiration() {
		return jwtConfig.getRefreshTokenExpiration();
	}

	public Long getAbsoluteSessionExpiration() {
		return jwtConfig.getAbsoluteSessionExpiration();
	}

	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	private Claims extractAllClaims(String token) {
		return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
	}

	private <T> T extractClaim(String jwtToken, Function<Claims, T> resolver) {
		Claims claims = extractAllClaims(jwtToken);
		return resolver.apply(claims);
	}
}
