package com.isms.identity.entity;

import java.time.Instant;
import java.util.UUID;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
//@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@EntityListeners(AuditingEntityListener.class)
@ToString
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "id", updatable = false, nullable = false)
	private UUID id;

	@Column(nullable = false, unique = true, length = 255)
	private String email;

	@Column(nullable = false, unique = true, length = 100)
	private String username;

	@Column(name = "password_hash", nullable = false, length = 255)
	private String passwordHash;

	@Column(name = "first_name", length = 100)
	private String firstName;

	@Column(name = "last_name", length = 100)
	private String lastName;

	@Column(name = "phone_number", length = 20)
	private String phoneNumber;

	@Column(name = "email_verified", nullable = false)
	@Builder.Default
	private boolean emailVerified = false;

	@Column(name = "phone_verified", nullable = false)
	@Builder.Default
	private boolean phoneVerified = false;

	@Column(name = "is_active", nullable = false)
	@Builder.Default
	private boolean active = true;

	@Column(name = "is_locked", nullable = false)
	@Builder.Default
	private boolean locked = false;

	@Column(name = "failed_login_attempts", nullable = false)
	@Builder.Default
	private int failedLoginAttempts = 0;

	@Column(name = "last_login_at")
	private Instant lastLoginAt;

	@Column(name = "password_changed_at")
	private Instant passwordChangedAt;

	@CreatedDate
	@Column(name = "created_at", updatable = false, nullable = false)
	private Instant createdAt;

	@LastModifiedDate
	@Column(name = "updated_at", nullable = false)
	private Instant updatedAt;

	// Business / Domain Logic

	public void incrementFailedLoginAttempts() {
		this.failedLoginAttempts++;
		if (this.failedLoginAttempts >= 3) {
			this.locked = true;
		}
	}

	public void resetFailedLoginAttempts() {
		this.failedLoginAttempts = 0;
		this.locked = false;
	}

	public boolean isAccountNonLocked() {
		return !this.locked;
	}

	public boolean isEnabled() {
		return this.active;
	}
}
