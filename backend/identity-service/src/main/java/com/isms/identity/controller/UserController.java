package com.isms.identity.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.isms.identity.dto.request.RegisterRequest;
import com.isms.identity.dto.request.UpdateUserStatusRequest;
import com.isms.identity.dto.request.UserUpdateRequest;
import com.isms.identity.dto.response.ApiResponse;
import com.isms.identity.dto.response.PaginationResponse;
import com.isms.identity.dto.response.UserDetailsResponse;
import com.isms.identity.dto.response.UserListResponse;
import com.isms.identity.dto.response.UserResponse;
import com.isms.identity.service.KeycloakUserService;
import com.isms.identity.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Validated
@CrossOrigin(origins = { "http://localhost:4200" })
public class UserController {

	private final UserService userService;
	private final KeycloakUserService keycloakUserService;

	@GetMapping
	@PreAuthorize("hasAnyRole('ISMS_ADMIN')")
	public ResponseEntity<ApiResponse<PaginationResponse<UserListResponse>>> getAllUsers(
			@RequestParam(defaultValue = "0") int pageIndex, @RequestParam(defaultValue = "5") int pageSize,
			@RequestParam(required = false) String search) {

		PaginationResponse<UserListResponse> users = keycloakUserService.getUsers(pageIndex, pageSize, search);

		ApiResponse<PaginationResponse<UserListResponse>> response = ApiResponse.success(users,
				"Users fetched successfully");

		return ResponseEntity.ok(response);
	}

	@PatchMapping("/{userId}/status")
	public ResponseEntity<ApiResponse<Void>> updateUserStaus(@PathVariable String userId,
			@RequestBody UpdateUserStatusRequest statusRequest) {

		keycloakUserService.updateUserStatus(userId, statusRequest.enabled());
		ApiResponse<Void> response = ApiResponse.success(null,
				statusRequest.enabled() ? "User enabled successfully" : "User disabled successfully");

		return ResponseEntity.ok(response);
	}

	@GetMapping("/{userId}")
	public ResponseEntity<ApiResponse<UserDetailsResponse>> getUser(@PathVariable String userId) {

		UserDetailsResponse user = keycloakUserService.getUser(userId);
		ApiResponse<UserDetailsResponse> response = ApiResponse.success(user, "User details success");

		return ResponseEntity.ok(response);
	}

	@GetMapping("/me")
	public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser(@AuthenticationPrincipal Jwt jwt) {

		UserResponse user = userService.syncUser(jwt);

		ApiResponse<UserResponse> response = ApiResponse.success(user, "User fetched successfully");

		return ResponseEntity.ok(response);
	}

	@PutMapping("/me")
	public ResponseEntity<ApiResponse<UserResponse>> updateCurrentUser(@AuthenticationPrincipal Jwt jwt,
			@Valid @RequestBody UserUpdateRequest updateRequest) {

		UserResponse updatedUser = userService.updateUser(jwt, updateRequest);

		ApiResponse<UserResponse> response = ApiResponse.success(updatedUser, "User details updated successfully");

		return ResponseEntity.ok(response);
	}

	@PostMapping
	public ResponseEntity<ApiResponse<String>> createUser(@AuthenticationPrincipal Jwt jwt,
			@Valid @RequestBody RegisterRequest registerRequest) {

		keycloakUserService.createUser(registerRequest);

		ApiResponse<String> response = ApiResponse.success(null, "User created successfully");

		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}
}