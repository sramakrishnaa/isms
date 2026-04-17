package com.isms.identity.repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.isms.identity.entity.RefreshToken;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

	List<RefreshToken> findByUserId(UUID userId);

	Optional<RefreshToken> findByTokenHash(String tokenHash);

	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query("""
			UPDATE RefreshToken rt
			SET    rt.revoked   = true,
			       rt.revokedAt = :now
			WHERE  rt.userId    = :userId
			  AND  rt.revoked   = false
			""")
	void revokeAllTokensByUserId(UUID userId, Instant now);

	@Modifying
	@Query("DELETE FROM RefreshToken rt WHERE rt.expiresAt < :now")
	void deleteExpiredTokens(Instant now);
}
