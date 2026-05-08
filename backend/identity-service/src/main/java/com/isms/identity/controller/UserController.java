package com.isms.identity.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.isms.identity.dto.request.RegisterRequest;
import com.isms.identity.dto.request.UserUpdateRequest;
import com.isms.identity.dto.response.ApiResponse;
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
	public ResponseEntity<ApiResponse<List<UserListResponse>>> getAllUsers(@AuthenticationPrincipal Jwt jwt) {

		List<UserListResponse> users = keycloakUserService.getUsers();

		ApiResponse<List<UserListResponse>> response = ApiResponse.success(users, "Users fetched successfully");

		return ResponseEntity.ok(response);
	}

	@GetMapping("/me")
	public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser(@AuthenticationPrincipal Jwt jwt) {

		UserResponse user = userService.syncUser(jwt);

		ApiResponse<UserResponse> response = ApiResponse.success(user, "User fetched successfully");

		return ResponseEntity.ok(response);
	}

	@PatchMapping("/me")
	public ResponseEntity<ApiResponse<UserResponse>> updateCurrentUser(@AuthenticationPrincipal Jwt jwt,
			@Valid @RequestBody UserUpdateRequest updateRequest) {

		UserResponse updatedUser = userService.updateUser(jwt, updateRequest);

		ApiResponse<UserResponse> response = ApiResponse.success(updatedUser, "User details updated successfully");

		return ResponseEntity.ok(response);
	}

	@PostMapping
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ApiResponse<String>> createUser(@AuthenticationPrincipal Jwt jwt,
			@Valid @RequestBody RegisterRequest registerRequest) {

		keycloakUserService.createUser(registerRequest);

		ApiResponse<String> response = ApiResponse.success(null, "User created successfully");

		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}
}