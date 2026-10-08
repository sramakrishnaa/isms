package com.isms.identity.service;

import com.isms.identity.dto.request.CreateUserRequest;
import com.isms.identity.dto.request.RequiredUserAction;
import com.isms.identity.dto.request.UpdateUserRequest;
import com.isms.identity.dto.response.PaginationResponse;
import com.isms.identity.dto.response.UserListItem;
import com.isms.identity.dto.response.UserResponse;
import com.isms.identity.exception.InvalidRequestException;
import com.isms.identity.exception.KeycloakOperationException;
import com.isms.identity.exception.UserAlreadyExistsException;
import com.isms.identity.exception.UserNotFoundException;
import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.MappingsRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

	private final KeycloakUserService keycloakUserService;
	private final RoleService roleService;
	private final ModelMapper mapper;
	private final KeycloakUserMapper userMapper;

	// Get users
	public PaginationResponse<UserListItem> getUsers(int pageIndex, int pageSize, String search) {
		int offset = validatePageParameters(pageIndex, pageSize);
		try {
			UsersResource usersResource = keycloakUserService.users();
			String trimmedSearch = (search != null) ? search.strip() : null;
			boolean hasSearch = trimmedSearch != null && !trimmedSearch.isEmpty();
			List<UserRepresentation> userReps;
			long totalCount;
			if (hasSearch) {
				userReps = usersResource.search(trimmedSearch, offset, pageSize);
				totalCount = usersResource.count(trimmedSearch);
			} else {
				userReps = usersResource.list(offset, pageSize);
				totalCount = usersResource.count();
			}
			List<UserListItem> data = userReps.stream().map(u -> mapper.map(u, UserListItem.class)).toList();
			return PaginationResponse.<UserListItem>builder().list(data).totalCount(totalCount).build();

		} catch (Exception e) {
			throw new KeycloakOperationException("Failed to fetch users", e);
		}
	}

	private int validatePageParameters(int pageIndex, int pageSize) {
		if (pageIndex < 0)
			throw new InvalidRequestException("pageIndex must be >= 0");
		if (pageSize < 1 || pageSize > 200)
			throw new InvalidRequestException("pageSize must be between 1 and 200");
		long offsetLong = (long) pageIndex * pageSize;
		if (offsetLong > Integer.MAX_VALUE)
			throw new InvalidRequestException("Pagination offset exceeds allowed range");
		return (int) offsetLong;
	}

	// Get a user
	public UserResponse getUser(String userId) {
		UserResource resource = keycloakUserService.user(userId);

		try {

			UserRepresentation rep = resource.toRepresentation();

			List<CredentialRepresentation> credentials = resource.credentials();

			MappingsRepresentation roleMappings = resource.roles().getAll();

			return userMapper.toUserResponse(rep, roleMappings, credentials);

		} catch (NotFoundException e) {
			throw new UserNotFoundException("User not found with id: " + userId);
		} catch (Exception e) {
			throw new KeycloakOperationException("Failed to fetch user details", e);
		}
	}

	// Create user
	public String createUser(CreateUserRequest request) {

		UserRepresentation user = userMapper.toUserRepresentation(request);
		try (Response response = keycloakUserService.users().create(user)) {
			String userId = switch (response.getStatus()) {
			case 201 -> extractUserId(response);
			case 409 -> throw new UserAlreadyExistsException("A user with this email address already exists");
			case 400 -> throw new KeycloakOperationException("Invalid user details submitted");
			default -> throw new KeycloakOperationException("Failed to create user. Status: " + response.getStatus());
			};

			sendUserActionEmail(userId, request);

			return userId;
		} catch (UserAlreadyExistsException | KeycloakOperationException e) {
			throw e;
		} catch (Exception e) {
			throw new KeycloakOperationException("Failed to create user", e);
		}

	}

	private String extractUserId(Response response) {
		String location = response.getHeaderString("Location");
		if (location == null || location.isBlank()) {
			throw new KeycloakOperationException("User created but user ID was not returned");
		}
		int lastSlashIndex = location.lastIndexOf('/');
		if (lastSlashIndex == -1 || lastSlashIndex == location.length() - 1) {
			throw new KeycloakOperationException("User created but user ID could not be extracted");
		}
		return location.substring(lastSlashIndex + 1);
	}

	private void sendUserActionEmail(String userId, CreateUserRequest request) {
		if (request.getRequiredActions() == null || request.getRequiredActions().isEmpty()) {
			return;
		}
		List<String> actions = request.getRequiredActions().stream().map(RequiredUserAction::getKeycloakValue).toList();
		keycloakUserService.users().get(userId).executeActionsEmail(actions);
	}

	// Update user
	public void updateUser(String userId, UpdateUserRequest request) {

		try {
			validateUpdateRequest(request);

			UserResource resource = keycloakUserService.user(userId);

			UserRepresentation user = resource.toRepresentation();

			boolean emailChanged = updateUserFields(user, request);

			resource.update(user);

			if (emailChanged) {
				resource.executeActionsEmail(List.of("VERIFY_EMAIL"));
			}

		} catch (NotFoundException e) {
			throw new UserNotFoundException("User not found: " + userId);

		} catch (ClientErrorException e) {

			int status = e.getResponse().getStatus();

			switch (status) {
			case 400 -> throw new InvalidRequestException("Invalid user update request");

			case 403 -> throw new KeycloakOperationException("Not authorized to update user", e);

			case 409 -> throw new UserAlreadyExistsException("Email already exists");

			default -> throw new KeycloakOperationException("Failed to update user", e);
			}

		} catch (Exception e) {
			throw new KeycloakOperationException("Failed to update user", e);
		}
	}

	private void validateUpdateRequest(UpdateUserRequest request) {
		if (request.getEmail() == null && request.getFirstName() == null && request.getLastName() == null) {
			throw new InvalidRequestException("At least one field must be provided for update");
		}
	}

	private boolean updateUserFields(UserRepresentation user, UpdateUserRequest request) {

		boolean emailChanged = false;

		if (request.getEmail() != null && !request.getEmail().equalsIgnoreCase(user.getEmail())) {

			user.setEmail(request.getEmail());
			user.setEmailVerified(false);

			emailChanged = true;
		}

		if (request.getFirstName() != null) {
			user.setFirstName(request.getFirstName());
		}

		if (request.getLastName() != null) {
			user.setLastName(request.getLastName());
		}

		return emailChanged;
	}

	// Update user status
	public void updateUserStatus(String userId, boolean enabled) {
		UserResource resource = keycloakUserService.user(userId);
		try {
			UserRepresentation rep = resource.toRepresentation();
			rep.setEnabled(enabled);
			resource.update(rep);
		} catch (NotFoundException e) {
			throw new UserNotFoundException("User not found with id: " + userId);
		} catch (Exception e) {
			throw new KeycloakOperationException("Failed to update user status", e);
		}
	}

	// Delete user
	public void deleteUser(String userId) {
		UserResource resource = keycloakUserService.user(userId);
		try {
			resource.remove();
		} catch (NotFoundException e) {
			throw new UserNotFoundException("User not found: " + userId);
		} catch (Exception e) {
			throw new KeycloakOperationException("Failed to delete user", e);
		}
	}
}
