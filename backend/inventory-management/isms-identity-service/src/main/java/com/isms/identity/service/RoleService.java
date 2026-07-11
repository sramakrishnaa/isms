package com.isms.identity.service;

import java.util.List;

import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.isms.identity.exception.KeycloakOperationException;

import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoleService {

	@Value("${keycloak.default-role}")
	private String defaultRole;

	private final EmailService emailService;
	private final KeycloakUserService keycloakUserService;

	protected List<String> getUserRoles(UserResource resource) {
		return resource.roles().clientLevel(keycloakUserService.getClientUUID()).listEffective().stream()
				.map(RoleRepresentation::getName).toList();
	}

	protected void assignDefaultRole(String userID) {
		assignClientRole(userID, this.defaultRole);
		emailService.sendVerificationLink(userID);
	}

	private String getClientUUID() {
		return keycloakUserService.getClientUUID();
	}

	private void assignClientRole(String userId, String role) {
		try {
			assignRolesToUser(userId, List.of(role));
		} catch (NotFoundException e) {
			throw new KeycloakOperationException("Role not found: " + role);
		} catch (Exception e) {
			throw new KeycloakOperationException("Failed to assign role", e);
		}
	}

	private void assignRolesToUser(String userId, List<String> roleNames) {
		List<RoleRepresentation> roles = getClientRoles(roleNames);
		keycloakUserService.users().get(userId).roles().clientLevel(keycloakUserService.getClientUUID()).add(roles);
	}

	private List<RoleRepresentation> getClientRoles(List<String> roleNames) {
		return roleNames.stream().map(name -> keycloakUserService.getRealm().clients().get(getClientUUID()).roles()
				.get(name).toRepresentation()).toList();
	}

	public void updateUserRoles(String userId, List<String> roles) {
		UserResource user = keycloakUserService.user(userId);
		replaceClientRoles(user, roles);
	}

	private void replaceClientRoles(UserResource resource, List<String> newRoleNames) {
		List<RoleRepresentation> current = resource.roles().clientLevel(getClientUUID()).listAll();
		if (!current.isEmpty()) {
			resource.roles().clientLevel(getClientUUID()).remove(current);
		}
		List<RoleRepresentation> incoming = getClientRoles(newRoleNames);
		resource.roles().clientLevel(getClientUUID()).add(incoming);
	}
}
