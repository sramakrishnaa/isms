package com.isms.identity.service;

import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.isms.identity.dto.request.LoginRequest;
import com.isms.identity.dto.request.RegisterRequest;
import com.isms.identity.dto.response.TokenResponse;
import com.isms.identity.dto.response.UserResponse;
import com.isms.identity.entity.User;
import com.isms.identity.exception.DuplicateResourceException;
import com.isms.identity.repository.UserRepository;
import com.isms.identity.security.JwtTokenProvider;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional
public class AuthService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final AuthenticationManager authenticationManager;
	private final JwtTokenProvider tokenProvider;

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
		user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

		User savedUser = userRepository.save(user);
		return modelMapper.map(savedUser, UserResponse.class);
	}

	public TokenResponse login(LoginRequest credentials) {

		try {
			Authentication authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(credentials.getEmail(), credentials.getPassword()));
			System.err.println(authentication.getPrincipal());
			return new TokenResponse("login success");
		} catch (Exception e) {
			throw e;
		}
	}
}
