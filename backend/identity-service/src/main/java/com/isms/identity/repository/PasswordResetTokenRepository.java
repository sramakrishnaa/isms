package com.isms.identity.repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.isms.identity.entity.PasswordResetToken;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, UUID> {

	Optional<PasswordResetToken> findByToken(String token);

	@Modifying
	@Query("UPDATE PasswordResetToken prt SET prt.used = true WHERE prt.userId = :userId")
	void markAllAsUsedByUserId(UUID userId);

	@Modifying
	@Query("DELETE FROM PasswordResetToken prt WHERE prt.expiresAt < :now")
	void deleteExpiredTokens(Instant now);
}
