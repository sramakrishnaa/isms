package com.isms.identity.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.isms.identity.entity.RefreshToken;
import com.isms.identity.exception.UnauthorizedException;
import com.isms.identity.repository.RefreshTokenRepository;
import com.isms.identity.security.JwtTokenProvider;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

	private final RefreshTokenRepository refreshTokenRepository;
	private final JwtTokenProvider jwtTokenProvider;

	private String generateToken() {
		return UUID.randomUUID() + "." + UUID.randomUUID();
	}

	private String hashToken(String token) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			return Base64.getEncoder().encodeToString(digest.digest(token.getBytes(StandardCharsets.UTF_8)));
		} catch (Exception e) {
			throw new IllegalStateException("Token hashing failed", e);
		}
	}

	private String extractUserAgent(HttpServletRequest r) {
		return r.getHeader("User-Agent");
	}

	private String extractDeviceInfo(HttpServletRequest r) {
		String ua = extractUserAgent(r);
		if (ua == null)
			return "Unknown";
		if (ua.contains("Mobile"))
			return "Mobile";
		return "Desktop";
	}

	private String extractIpAddress(HttpServletRequest r) {
		String xff = r.getHeader("X-Forwarded-For");
		return (xff != null) ? xff.split(",")[0] : r.getRemoteAddr();
	}

	private RefreshToken buildRefreshToken(UUID userId, String hash, HttpServletRequest request) {
		return RefreshToken.builder().userId(userId).tokenHash(hash).deviceInfo(extractDeviceInfo(request))
				.ipAddress(extractIpAddress(request)).userAgent(extractUserAgent(request))
				.expiresAt(Instant.now().plusMillis(jwtTokenProvider.getRefreshTokenExpiration())).build();
	}

	@Transactional
	public String issueRefreshToken(UUID userId, HttpServletRequest request) {
		String rawToken = generateToken();
		String hash = hashToken(rawToken);
		RefreshToken refreshToken = buildRefreshToken(userId, hash, request);
		refreshTokenRepository.save(refreshToken);
		return rawToken;
	}

	public RefreshToken validateRefreshToken(String rawToken) {
		String hash = hashToken(rawToken);
		RefreshToken token = refreshTokenRepository.findByTokenHash(hash)
				.orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));
		if (!token.isValid()) {
			throw new UnauthorizedException("Refresh token expired or revoked");
		}
		return token;
	}

	@Transactional
	public String rotateRefreshToken(String rawToken, HttpServletRequest request) {
		RefreshToken old = validateRefreshToken(rawToken);
		if (old.getReplacedByTokenId() != null) {
			refreshTokenRepository.revokeAllTokensByUserId(old.getUserId());
			throw new UnauthorizedException("Refresh token reuse detected");
		}
		String newRaw = generateToken();
		String newHash = hashToken(newRaw);
		RefreshToken newToken = buildRefreshToken(old.getUserId(), newHash, request);
		refreshTokenRepository.save(newToken);
		old.revoke(newToken.getId());
		refreshTokenRepository.save(old);
		return newRaw;
	}

	@Transactional
	public void logoutAll(UUID userId) {
		refreshTokenRepository.revokeAllTokensByUserId(userId);
	}

	@Transactional
	public void logout(String rawToken) {
		String hashed = hashToken(rawToken);
		refreshTokenRepository.findByTokenHash(hashed).ifPresent(token -> {
			token.revoke(null);
			refreshTokenRepository.save(token);
		});
	}

}
