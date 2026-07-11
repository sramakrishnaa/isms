package com.isms.identity.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.isms.identity.dto.request.CreateUserRequest;
import com.isms.identity.dto.request.UpdateUserStatusRequest;
import com.isms.identity.dto.response.ApiResponse;
import com.isms.identity.dto.response.PaginationResponse;
import com.isms.identity.dto.response.UserResponse;
import com.isms.identity.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

	private final UserService userService;

	@GetMapping
	public ResponseEntity<ApiResponse<PaginationResponse<UserResponse>>> getUsers(
			@RequestParam(defaultValue = "0") int pageIndex, @RequestParam(defaultValue = "5") int pageSize,
			@RequestParam(required = false) String search) {

		PaginationResponse<UserResponse> users = userService.getUsers(pageIndex, pageSize, search);

		ApiResponse<PaginationResponse<UserResponse>> response = ApiResponse.success("Users fetched successfully",
				users);

		return ResponseEntity.ok(response);
	}

	@GetMapping("/{userId}")
	public ResponseEntity<ApiResponse<UserResponse>> getUser(@PathVariable String userId) {

		UserResponse user = userService.getUser(userId);
		ApiResponse<UserResponse> response = ApiResponse.success("User details success", user);

		return ResponseEntity.ok(response);
	}

	@PostMapping
	@PreAuthorize("hasAnyRole('ADMIN')")
	public ResponseEntity<ApiResponse<String>> createUser(@AuthenticationPrincipal Jwt jwt,
			@Valid @RequestBody CreateUserRequest createUserRequest) {

		String userId = userService.createUser(createUserRequest);

		ApiResponse<String> response = ApiResponse.success("User created successfully", userId);

		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@PatchMapping("/status/{userId}")
	public ResponseEntity<ApiResponse<Void>> updateUserStaus(@PathVariable String userId,
			@RequestBody UpdateUserStatusRequest statusRequest) {

		userService.updateUserStatus(userId, statusRequest.enabled());
		ApiResponse<Void> response = ApiResponse
				.success(statusRequest.enabled() ? "User enabled successfully" : "User disabled successfully", null);

		return ResponseEntity.ok(response);
	}

	@DeleteMapping("/{userId}")
	public ResponseEntity<ApiResponse<String>> deletUser(@PathVariable String userId) {
		userService.deleteUser(userId);
		return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("User deleted successfully"));
	}

}
