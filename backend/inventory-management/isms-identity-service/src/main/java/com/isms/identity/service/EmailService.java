package com.isms.identity.service;

import com.isms.identity.exception.KeycloakOperationException;
import com.isms.identity.exception.UserNotFoundException;
import jakarta.ws.rs.NotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.resource.UserResource;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

	private final KeycloakUserService keycloakUserService;

	public void sendVerificationLink(String userId) {
		UserResource resource = keycloakUserService.user(userId);
		try {
			resource.executeActionsEmail(List.of("VERIFY_EMAIL"));
		} catch (NotFoundException e) {
			throw new UserNotFoundException("User not found with id: " + userId);
		} catch (Exception e) {
			throw new KeycloakOperationException("Failed to send verification email", e);
		}
	}
}
