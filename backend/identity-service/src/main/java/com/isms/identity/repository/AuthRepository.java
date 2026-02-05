package com.isms.identity.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.isms.identity.entity.User;

@Repository
public interface AuthRepository extends JpaRepository<User, Long>{

}
