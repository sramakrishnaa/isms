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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "refresh_tokens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshToken {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "id", updatable = false, nullable = false)
	private UUID id;

	@Column(name = "user_id", nullable = false)
	private UUID userId;

	@Column(name = "token_hash", nullable = false, unique = true, length = 255)
	private String tokenHash;

	@Column(name = "device_info")
	private String deviceInfo;

	@Column(name = "ip_address", length = 45)
	private String ipAddress;

	@Column(name = "user_agent", columnDefinition = "TEXT")
	private String userAgent;

	@Column(name = "expires_at", nullable = false)
	private Instant expiresAt;

	@Column(name = "revoked", nullable = false)
	private boolean revoked;

	@Column(name = "revoked_at")
	private Instant revokedAt;

	@Column(name = "replaced_by_token_id")
	private UUID replacedByTokenId;

	@Column(name = "created_at", updatable = false, nullable = false)
	private Instant createdAt;

	@PrePersist
	void onCreate() {
		this.createdAt = Instant.now();
		this.revoked = false;
	}

	public boolean isExpired() {
		return Instant.now().isAfter(this.expiresAt);
	}

	public boolean isValid() {
		return !revoked && !isExpired();
	}

	public void revoke(UUID replacedBy) {
		this.revoked = true;
		this.revokedAt = Instant.now();
		this.replacedByTokenId = replacedBy;
	}
}
