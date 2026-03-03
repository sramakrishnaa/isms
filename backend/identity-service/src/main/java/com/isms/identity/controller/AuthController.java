package com.isms.identity.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.isms.identity.dto.request.LoginRequest;
import com.isms.identity.dto.request.LogoutRequest;
import com.isms.identity.dto.request.RefreshTokenRequest;
import com.isms.identity.dto.request.RegisterRequest;
import com.isms.identity.dto.response.ApiResponse;
import com.isms.identity.dto.response.LoginResponse;
import com.isms.identity.dto.response.UserResponse;
import com.isms.identity.service.AuthService;
import com.isms.identity.util.SecurityUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

	private final AuthService authService;

	@PostMapping("/signup")
	public ResponseEntity<ApiResponse<UserResponse>> signup(@Valid @RequestBody RegisterRequest request) {
		UserResponse createdAccount = authService.register(request);
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(ApiResponse.success(createdAccount, "Account Created Successfully"));
	}

	@PostMapping("/login")
	public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request,
			HttpServletRequest httpRequest) {
		LoginResponse response = authService.login(request, httpRequest);
		return ResponseEntity.ok(ApiResponse.success(response, "Login successful"));
	}

	@PostMapping("/refresh")
	public ResponseEntity<ApiResponse<LoginResponse>> refreshToken(@Valid @RequestBody RefreshTokenRequest request,
			HttpServletRequest httpRequest) {
		LoginResponse response = authService.refreshToken(request, httpRequest);
		return ResponseEntity.ok(ApiResponse.success(response, "Token refreshed successfully"));
	}

	@PostMapping("/logout")
	public ResponseEntity<ApiResponse<Void>> logout(@Valid @RequestBody LogoutRequest request) {
		authService.logout(request.getRefreshToken());
		return ResponseEntity.ok(ApiResponse.success(null, "Logged out successfully"));
	}

	@PostMapping("/logout-all")
	public ResponseEntity<ApiResponse<Void>> logoutAll() {
		UUID userId = SecurityUtils.getCurrentUserId();
		authService.logoutAll(userId);
		return ResponseEntity.ok(ApiResponse.success(null, "Logged out from all devices"));
	}

}
