package com.isms.identity.service;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.isms.identity.dto.request.RegisterRequest;
import com.isms.identity.dto.response.UserResponse;
import com.isms.identity.entity.User;
import com.isms.identity.exception.DuplicateResourceException;
import com.isms.identity.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional
public class AuthService {

	private final UserRepository userRepository;
//	private final PasswordEncoder passwordEncoder;

	private final ModelMapper modelMapper;

	public UserResponse createUser(RegisterRequest registerRequest) {

		String normalizedEmail = registerRequest.getEmail().trim().toLowerCase();
		String normalizedPhoneNumber = registerRequest.getPhoneNumber().trim();
		String normalizedName = registerRequest.getName().trim();

		if (userRepository.existsByEmail(normalizedEmail)) {
			throw new DuplicateResourceException("Email address is already registered");
		}

		if (userRepository.existsByPhoneNumber(normalizedPhoneNumber)) {
			throw new DuplicateResourceException("Phone number is already registered");
		}

		User user = new User();

		user.setEmail(normalizedEmail);
		user.setPhoneNumber(normalizedPhoneNumber);
		user.setName(normalizedName);
		user.setPassword(registerRequest.getPassword());
//		user.setPassword(passwordEncoder.encode(dto.getPassword()));

		User savedUser = userRepository.save(user);
		return modelMapper.map(savedUser, UserResponse.class);
	}
}
