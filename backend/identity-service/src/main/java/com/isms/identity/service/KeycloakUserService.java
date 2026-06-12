package com.isms.identity.service;

import java.util.List;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.isms.identity.dto.request.RegisterRequest;
import com.isms.identity.dto.response.PaginationResponse;
import com.isms.identity.dto.response.UserListResponse;

import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KeycloakUserService {

	@Value("${keycloak.realm}")
	private String realm;

	private final Keycloak keycloak;

	private UsersResource getAllUsers() {
		return keycloak.realm(realm).users();
	}

	public PaginationResponse<UserListResponse> getUsers(int pageIndex, int pageSize, String search) {

		List<UserRepresentation> userRepresentations;

		long totalCount;

		if (search != null && !search.isBlank()) {
			userRepresentations = getAllUsers().search(search, pageIndex * pageSize, pageSize);
			totalCount = getAllUsers().search(search, 0, Integer.MAX_VALUE).size();
		} else {
			userRepresentations = getAllUsers().list(pageIndex * pageSize, pageSize);
			totalCount = getAllUsers().count();
		}

		List<UserListResponse> list = userRepresentations.stream()
				.map(user -> UserListResponse.builder().id(user.getId()).username(user.getUsername())
						.firstName(user.getFirstName()).lastName(user.getLastName()).email(user.getEmail())
						.emailVerified(Boolean.TRUE.equals(user.isEmailVerified()))
						.enabled(Boolean.TRUE.equals(user.isEnabled())).createdTimestamp(user.getCreatedTimestamp())
						.build())
				.toList();
		return new PaginationResponse<UserListResponse>(list, totalCount);
	}

	public String createUser(RegisterRequest registerRequest) {

		List<UserRepresentation> existingUsers = getAllUsers().searchByEmail(registerRequest.getEmail(), true);

		if (!existingUsers.isEmpty()) {
			throw new RuntimeException("Email already exists");
		}

		UserRepresentation user = new UserRepresentation();
		user.setEnabled(true);
		user.setEmail(registerRequest.getEmail());

		CredentialRepresentation credential = new CredentialRepresentation();
		credential.setType(CredentialRepresentation.PASSWORD);
		credential.setValue("Temp@123");
		credential.setTemporary(true);

		user.setCredentials(List.of(credential));

		Response response = keycloak.realm("isms").users().create(user);

		try {

			if (response.getStatus() == 201) {
				return "User created successfully";
			}

			if (response.getStatus() == 409) {
				throw new RuntimeException("Email already exists");
			}

			throw new RuntimeException("Failed to create user");

		} finally {
			response.close();
		}

	}

}
