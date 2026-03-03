package com.isms.identity.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.isms.identity.entity.RefreshToken;
import com.isms.identity.exception.UnauthorizedException;
import com.isms.identity.repository.RefreshTokenRepository;
import com.isms.identity.security.JwtTokenProvider;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

	private final RefreshTokenRepository refreshTokenRepository;
	private final JwtTokenProvider jwtTokenProvider;
	private final TokenRevokeService tokenRevokeService;

	public record RotationResult(String newRawToken, UUID userId) {
	}

	@Transactional
	public String issueRefreshToken(UUID userId, HttpServletRequest request) {
		String rawToken = generateToken();
		RefreshToken refreshToken = buildRefreshToken(userId, rawToken,
				Instant.now().plusMillis(jwtTokenProvider.getAbsoluteSessionExpiration()), request);
		refreshTokenRepository.save(refreshToken);
		return rawToken;
	}

	@Transactional
	public RotationResult rotateRefreshToken(String rawToken, HttpServletRequest request) {
		RefreshToken oldToken = refreshTokenRepository.findByTokenHash(hashToken(rawToken))
				.orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));

		if (oldToken.getReplacedByTokenId() != null) {
			tokenRevokeService.revokeAllSessionsOnReuseDetection(oldToken);
			throw new UnauthorizedException("Refresh token reuse detected. All sessions have been revoked.");
		}

		if (!oldToken.isValid()) {
			throw new UnauthorizedException("Refresh token expired or revoked");
		}

		String newRaw = generateToken();
		RefreshToken newToken = buildRefreshToken(oldToken.getUserId(), newRaw, oldToken.getAbsoluteExpiresAt(),
				request);
		refreshTokenRepository.save(newToken);

		oldToken.revoke(newToken.getId());
		refreshTokenRepository.save(oldToken);

		return new RotationResult(newRaw, oldToken.getUserId());
	}

	@Transactional
	public void logout(String rawToken) {
		refreshTokenRepository.findByTokenHash(hashToken(rawToken)).ifPresent(token -> {
			token.revoke(null);
			refreshTokenRepository.save(token);
		});
	}

	@Transactional
	public void logoutAll(UUID userId) {
		refreshTokenRepository.revokeAllTokensByUserId(userId, Instant.now());
	}

	private String generateToken() {
		return UUID.randomUUID() + "." + UUID.randomUUID();
	}

	private String hashToken(String token) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
			return Base64.getEncoder().encodeToString(hash);
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException("SHA-256 algorithm not available", e);
		}
	}

	private RefreshToken buildRefreshToken(UUID userId, String rawToken, Instant absoluteExpiresAt,
			HttpServletRequest request) {
		return RefreshToken.builder().userId(userId).tokenHash(hashToken(rawToken))
				.deviceInfo(extractDeviceInfo(request)).ipAddress(extractIpAddress(request))
				.userAgent(extractUserAgent(request))
				.expiresAt(Instant.now().plusMillis(jwtTokenProvider.getRefreshTokenExpiration()))
				.absoluteExpiresAt(absoluteExpiresAt).build();
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void revokeAllSessionsOnReuseDetection(RefreshToken token) {
		token.markReuseDetected();
		refreshTokenRepository.save(token);
		refreshTokenRepository.revokeAllTokensByUserId(token.getUserId(), Instant.now());
	}

	private String extractIpAddress(HttpServletRequest request) {
		String xff = request.getHeader("X-Forwarded-For");
		return (xff != null && !xff.isBlank()) ? xff.split(",")[0].trim() : request.getRemoteAddr();
	}

	private String extractUserAgent(HttpServletRequest request) {
		return request.getHeader("User-Agent");
	}

	private String extractDeviceInfo(HttpServletRequest request) {
		String ua = extractUserAgent(request);
		if (ua == null || ua.isBlank())
			return "Unknown";
		if (ua.contains("Mobile"))
			return "Mobile";
		return "Desktop";
	}
}