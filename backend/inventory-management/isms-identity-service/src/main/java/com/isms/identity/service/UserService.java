package com.isms.identity.service;

import java.util.List;

import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.isms.identity.dto.request.CreateUserRequest;
import com.isms.identity.dto.request.UpdateUserRequest;
import com.isms.identity.dto.response.PaginationResponse;
import com.isms.identity.dto.response.UserResponse;
import com.isms.identity.exception.InvalidRequestException;
import com.isms.identity.exception.KeycloakOperationException;
import com.isms.identity.exception.UserAlreadyExistsException;
import com.isms.identity.exception.UserNotFoundException;

import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	private final KeycloakUserService keycloakUserService;
	private final RoleService roleService;
	private final ModelMapper mapper;
	private final KeycloakUserMapper userMapper;

	// Get users
	public PaginationResponse<UserResponse> getUsers(int pageIndex, int pageSize, String search) {
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
			List<UserResponse> data = userReps.stream().map(u -> mapper.map(u, UserResponse.class)).toList();
			return PaginationResponse.<UserResponse>builder().list(data).totalCount(totalCount).build();

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
			List<String> roles = roleService.getUserRoles(resource);
			return userMapper.toUserResponse(rep, roles);
		} catch (NotFoundException e) {
			throw new UserNotFoundException("User not found with id: " + userId);
		} catch (Exception e) {
			throw new KeycloakOperationException("Failed to fetch user details", e);
		}
	}

	// Create user
	public String createUser(CreateUserRequest request) {
		UserRepresentation rep = userMapper.toUserRepresentation(request);
		try (Response response = keycloakUserService.users().create(rep)) {
			return switch (response.getStatus()) {
			case 201 -> {
				String location = response.getHeaderString("Location");
				if (location == null)
					throw new KeycloakOperationException("User created but ID not returned");
				String userId = location.substring(location.lastIndexOf('/') + 1);
				roleService.assignDefaultRole(userId);
				yield userId;
			}
			case 409 -> throw new UserAlreadyExistsException("Username or email already exists");
			case 400 -> throw new KeycloakOperationException("Invalid user details submitted");
			default -> throw new KeycloakOperationException("Failed to create user. Status: " + response.getStatus());
			};
		} catch (UserAlreadyExistsException | KeycloakOperationException e) {
			throw e;
		} catch (Exception e) {
			throw new KeycloakOperationException("Failed to create user", e);
		}
	}

	// Update user
	public void updateUser(String userId, UpdateUserRequest request) {
		validateUpdateRequest(request);
		UserResource resource = keycloakUserService.user(userId);
		try {
			UserRepresentation rep = resource.toRepresentation();
			boolean emailChanged = false;

			if (request.getEmail() != null) {
				rep.setEmail(request.getEmail());
				rep.setEmailVerified(false);
				emailChanged = true;
			}
			if (request.getFirstName() != null) {
				rep.setFirstName(request.getFirstName());
			}
			if (request.getLastName() != null) {
				rep.setLastName(request.getLastName());
			}

			resource.update(rep);

			if (emailChanged) {
				resource.executeActionsEmail(List.of("VERIFY_EMAIL"));
			}
		} catch (NotFoundException e) {
			throw new UserNotFoundException("User not found: " + userId);
		} catch (InvalidRequestException | IllegalArgumentException e) {
			throw e;
		} catch (Exception e) {
			throw new KeycloakOperationException("Failed to update user", e);
		}
	}

	private void validateUpdateRequest(UpdateUserRequest request) {
		if (request.getEmail() == null && request.getFirstName() == null && request.getLastName() == null) {
			throw new InvalidRequestException("At least one field must be provided for update");
		}
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