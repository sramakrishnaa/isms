package com.isms.identity.entity;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "refresh_tokens")
@Getter
@NoArgsConstructor
public class RefreshToken {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "id", updatable = false, nullable = false)
	private UUID id;

	@Version
	@Column(name = "version", nullable = false)
	private Long version;

	@Column(name = "user_id", nullable = false, updatable = false)
	private UUID userId;

	@Column(name = "token_hash", nullable = false, unique = true, updatable = false, length = 255)
	private String tokenHash;

	@Column(name = "device_info", updatable = false)
	private String deviceInfo;

	@Column(name = "ip_address", length = 45, updatable = false)
	private String ipAddress;

	@Column(name = "user_agent", columnDefinition = "TEXT", updatable = false)
	private String userAgent;

	@Column(name = "expires_at", nullable = false, updatable = false)
	private Instant expiresAt;

	@Column(name = "absolute_expires_at", nullable = false, updatable = false)
	private Instant absoluteExpiresAt;

	@Column(name = "revoked", nullable = false)
	private boolean revoked = false;

	@Column(name = "revoked_at")
	private Instant revokedAt;

	@Column(name = "replaced_by_token_id")
	private UUID replacedByTokenId;

	@Column(name = "reuse_detected_at")
	private Instant reuseDetectedAt;

	@Column(name = "created_at", updatable = false, nullable = false)
	private Instant createdAt;

	@Builder
	public RefreshToken(UUID userId, String tokenHash, String deviceInfo, String ipAddress, String userAgent,
			Instant expiresAt, Instant absoluteExpiresAt) {
		this.userId = userId;
		this.tokenHash = tokenHash;
		this.deviceInfo = deviceInfo;
		this.ipAddress = ipAddress;
		this.userAgent = userAgent;
		this.expiresAt = expiresAt;
		this.absoluteExpiresAt = absoluteExpiresAt;
		this.revoked = false;
	}

	@PrePersist
	void onCreate() {
		this.createdAt = Instant.now();
		this.revoked = false;
	}

	public boolean isExpired() {
		Instant now = Instant.now();
		return now.isAfter(this.expiresAt) || now.isAfter(this.absoluteExpiresAt);
	}

	public boolean isValid() {
		return !revoked && !isExpired();
	}

	public void revoke(UUID replacedByTokenId) {
		this.revoked = true;
		this.revokedAt = Instant.now();
		this.replacedByTokenId = replacedByTokenId;
	}

	public void markReuseDetected() {
		this.reuseDetectedAt = Instant.now();
	}
}