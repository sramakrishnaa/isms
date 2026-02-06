package com.isms.identity.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.isms.identity.entity.User;
import java.util.List;
import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	boolean existsByEmail(String email);
	boolean existsByPhoneNumber(String phoneNumber);
	
	Optional<User> findByEmail(String email);
}
