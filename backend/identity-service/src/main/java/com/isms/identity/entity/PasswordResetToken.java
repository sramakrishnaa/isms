package com.isms.identity.entity;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "password_reset_tokens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasswordResetToken {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "id", updatable = false, nullable = false)
	private UUID id;

	@Column(name = "user_id", nullable = false, columnDefinition = "BINARY(16)")
	private UUID userId;

	@Column(nullable = false, unique = true)
	private String token;

	@Column(name = "expires_at", nullable = false)
	private Instant expiresAt;

	@Column(name = "used")
	@Builder.Default
	private Boolean used = false;

	@Column(name = "created_at", updatable = false)
	@Builder.Default
	private Instant createdAt = Instant.now();

	public boolean isExpired() {
		return Instant.now().isAfter(this.expiresAt);
	}

	public boolean isValid() {
		return !this.used && !isExpired();
	}
}