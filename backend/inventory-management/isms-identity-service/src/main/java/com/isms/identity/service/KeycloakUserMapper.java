package com.isms.identity.service;

import java.util.List;

import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import com.isms.identity.dto.request.CreateUserRequest;
import com.isms.identity.dto.response.UserResponse;

@Service
public class KeycloakUserMapper {

	protected UserRepresentation toUserRepresentation(CreateUserRequest request) {
		UserRepresentation user = new UserRepresentation();
		user.setUsername(request.getUsername());
		user.setEmail(request.getEmail());
		user.setFirstName(request.getFirstName());
		user.setLastName(request.getLastName());
		user.setEnabled(true);
		user.setEmailVerified(false);
		user.setRequiredActions(List.of("UPDATE_PASSWORD", "VERIFY_EMAIL"));
		return user;
	}

	protected UserResponse toUserResponse(UserRepresentation user, List<String> roles) {
		return UserResponse.builder().id(user.getId()).username(user.getUsername()).firstName(user.getFirstName())
				.lastName(user.getLastName()).email(user.getEmail()).emailVerified(isTrue(user.isEmailVerified()))
				.enabled(isTrue(user.isEnabled())).createdTimestamp(user.getCreatedTimestamp()).roles(roles).build();
	}

	private boolean isTrue(Boolean value) {
		return Boolean.TRUE.equals(value);
	}
}
