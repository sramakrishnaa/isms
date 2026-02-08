package com.isms.identity.security;

import java.util.Date;
import java.util.UUID;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.isms.identity.config.JwtConfig;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

	private final JwtConfig jwtConfig;

	public String generateToken(UserDetails userDetails) {
		return Jwts.builder().setSubject(userDetails.getUsername()).setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + jwtConfig.getAccessTokenExpiration()))
				.signWith(getSigningKey(), SignatureAlgorithm.HS256).compact();
	}



	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}

//	public boolean isTokenValid(String token, UserDetails details) {
//		return extractUsername(token).equals(details.getUsername()) && !isTokenExpired(token);
//	}

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

	private SecretKey getSigningKey() {
		return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtConfig.getSecret()));
	}

	private <T> T extractClaim(String jwtToken, Function<Claims, T> resolver) {
		Claims claims = extractAllClaims(jwtToken);
		return resolver.apply(claims);
	}

	private Claims extractAllClaims(String token) {
		return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
	}

	private boolean isTokenExpired(String token) {
		return extractClaim(token, Claims::getExpiration).before(new Date());
	}

}
