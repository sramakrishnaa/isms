package com.isms.identity.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.isms.identity.common.ApiResponse;
import com.isms.identity.dto.request.LoginRequest;
import com.isms.identity.dto.request.RegisterRequest;
import com.isms.identity.dto.response.TokenResponse;
import com.isms.identity.dto.response.UserResponse;
import com.isms.identity.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private final AuthService authService;

	@PostMapping("signup")
	public ResponseEntity<ApiResponse<UserResponse>> createUser(@Valid @RequestBody RegisterRequest registerRequest) {
		UserResponse createdAccount = authService.createUser(registerRequest);
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(ApiResponse.success("Account Created Successfully", createdAccount));
	}

	@PostMapping("signin")
	public TokenResponse userLogin(@Valid @RequestBody LoginRequest credentials) {

		return authService.login(credentials);
	}

}
