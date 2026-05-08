package com.isms.identity.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.isms.identity.entity.User;

public interface UserRepository extends JpaRepository<User, UUID> {

	Optional<User> findByKeycloakUserId(String keycloakUserId);
}