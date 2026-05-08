package com.isms.identity.service;

import org.modelmapper.ModelMapper;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.isms.identity.dto.request.UserUpdateRequest;
import com.isms.identity.dto.response.UserResponse;
import com.isms.identity.entity.User;
import com.isms.identity.exception.UserNotFoundException;
import com.isms.identity.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final ModelMapper mapper;

	@Transactional
	public UserResponse syncUser(Jwt jwt) {

		String keycloakUserId = jwt.getSubject();

		return userRepository.findByKeycloakUserId(keycloakUserId).map(user -> mapper.map(user, UserResponse.class))
				.orElseGet(() -> {

					User user = User.builder().keycloakUserId(keycloakUserId).email(jwt.getClaimAsString("email"))
							.username(jwt.getClaimAsString("preferred_username"))
							.firstName(jwt.getClaimAsString("given_name")).lastName(jwt.getClaimAsString("family_name"))
							.build();

					return mapper.map(userRepository.save(user), UserResponse.class);
				});
	}

	@Transactional
	public UserResponse updateUser(Jwt jwt, UserUpdateRequest updateRequest) {
		String keycloakUserId = jwt.getSubject();
		return userRepository.findByKeycloakUserId(keycloakUserId).map(user -> {
			user.setFirstName(updateRequest.getFirstName());
			user.setLastName(updateRequest.getLastName());

			return mapper.map(userRepository.save(user), UserResponse.class);
		}).orElseThrow(() -> new UserNotFoundException("User not found"));
	}
}
