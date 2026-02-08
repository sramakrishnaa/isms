package com.isms.identity.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
@Transactional
public class RefreshTokenService {

	private final RefreshTokenRepository refreshTokenRepository;
	private final JwtTokenProvider jwtTokenProvider;

	public String createRefreshToken(UUID userId, HttpServletRequest request) {

		String rawToken = generateToken();
		String hash = hashToken(rawToken);

		RefreshToken token = RefreshToken.builder().userId(userId).tokenHash(hash)
				.deviceInfo(extractDeviceInfo(request)).ipAddress(extractIpAddress(request))
				.userAgent(extractUserAgent(request))
				.expiresAt(Instant.now().plusMillis(jwtTokenProvider.getRefreshTokenExpiration())).build();

		refreshTokenRepository.save(token);
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

	public String rotateRefreshToken(String rawToken, HttpServletRequest request) {

		RefreshToken oldToken = validateRefreshToken(rawToken);

		if (oldToken.getReplacedByTokenId() != null) {
			refreshTokenRepository.revokeAllTokensByUserId(oldToken.getUserId());
			throw new UnauthorizedException("Refresh token reuse detected");
		}

		String newRaw = generateToken();
		String newHash = hashToken(newRaw);

		RefreshToken newToken = RefreshToken.builder().userId(oldToken.getUserId()).tokenHash(newHash)
				.deviceInfo(extractDeviceInfo(request)).ipAddress(extractIpAddress(request))
				.userAgent(extractUserAgent(request))
				.expiresAt(Instant.now().plusMillis(jwtTokenProvider.getRefreshTokenExpiration())).build();

		refreshTokenRepository.save(newToken);

		oldToken.revoke(newToken.getId());
		refreshTokenRepository.save(oldToken);

		return newRaw;
	}



	private String generateToken() {
		return UUID.randomUUID() + "-" + UUID.randomUUID();
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
}
