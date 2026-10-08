package com.isms.identity.service;

import com.isms.identity.dto.request.CreateUserRequest;
import com.isms.identity.dto.request.RequiredUserAction;
import com.isms.identity.dto.response.UserResponse;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.MappingsRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

@Service
public class KeycloakUserMapper {

	protected UserRepresentation toUserRepresentation(CreateUserRequest request) {

		String email = request.getEmail().trim().toLowerCase(Locale.ROOT);

		UserRepresentation user = new UserRepresentation();
		user.setUsername(email);
		user.setEmail(email);
		user.setFirstName(request.getFirstName().trim());
		user.setLastName(request.getLastName().trim());

		user.setEnabled(request.isEnabled());
		user.setEmailVerified(request.isEmailVerified());

		if (request.getRequiredActions() != null) {
			user.setRequiredActions(
					request.getRequiredActions().stream().map(RequiredUserAction::getKeycloakValue).toList());
		}

		return user;
	}

	protected UserResponse toUserResponse(UserRepresentation user, MappingsRepresentation roles,
			List<CredentialRepresentation> credentials) {
		

		return UserResponse.builder().id(user.getId()).username(user.getUsername()).firstName(user.getFirstName())
				.lastName(user.getLastName()).email(user.getEmail()).emailVerified(isTrue(user.isEmailVerified()))
				.enabled(isTrue(user.isEnabled())).createdTimestamp(user.getCreatedTimestamp()).roles(roles)
				.credentials(credentials) 
				.build();
	}

	private boolean isTrue(Boolean value) {
		return Boolean.TRUE.equals(value);
	}
}
