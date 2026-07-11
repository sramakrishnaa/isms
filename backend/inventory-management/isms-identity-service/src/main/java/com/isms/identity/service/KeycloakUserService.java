package com.isms.identity.service;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.isms.identity.exception.KeycloakOperationException;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KeycloakUserService {

	@Value("${keycloak.realm}")
	private String realm;

	@Value("${keycloak.client-id}")
	private String clientId;

	private final Keycloak keycloak;

	private String clientUUID;

	@PostConstruct
	private void resolveClientUUID() {
		try {
			this.clientUUID = keycloak.realm(realm).clients().findByClientId(clientId).stream().findFirst()
					.orElseThrow(() -> new KeycloakOperationException("Keycloak client not found: " + clientId))
					.getId();
		} catch (Exception e) {
			System.err.println("Could not resolve Keycloak client UUID at startup. "
					+ "Will retry on first use. Reason: {}" + e.getMessage());
		}
	}

	protected UserResource user(String userId) {
		validateUserId(userId);
		return users().get(userId);
	}

	protected String getClientUUID() {
		if (clientUUID == null) {
			resolveClientUUID();
		}
		return clientUUID;
	}

	protected RealmResource getRealm() {
		return keycloak.realm(realm);
	}

	protected UsersResource users() {
		return keycloak.realm(realm).users();
	}

	private void validateUserId(String userId) {
		if (userId == null || userId.isBlank())
			throw new IllegalArgumentException("userId must not be blank");
	}
}
