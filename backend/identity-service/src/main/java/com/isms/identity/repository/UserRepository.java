package com.isms.identity.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.isms.identity.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

	boolean existsByEmail(String email);

	boolean existsByPhoneNumber(String phoneNumber);

	boolean existsByUsername(String username);

	Optional<User> findByEmail(String email);

	Optional<User> findByUsername(String username);

	@Query("SELECT u FROM User u WHERE u.email = :email AND u.active = true")
	Optional<User> findActiveUserByEmail(String email);
}
