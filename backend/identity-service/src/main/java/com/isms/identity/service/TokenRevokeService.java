package com.isms.identity.service;

import java.time.Instant;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.isms.identity.entity.RefreshToken;
import com.isms.identity.repository.RefreshTokenRepository;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Service
@RequiredArgsConstructor
public class TokenRevokeService {

	private final RefreshTokenRepository refreshTokenRepository;

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void revokeAllSessionsOnReuseDetection(RefreshToken token) {
		token.markReuseDetected();
		refreshTokenRepository.save(token);
		refreshTokenRepository.revokeAllTokensByUserId(token.getUserId(), Instant.now());
	}
}
