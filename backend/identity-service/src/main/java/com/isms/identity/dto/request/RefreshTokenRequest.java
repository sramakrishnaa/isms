package com.isms.identity.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class RefreshTokenRequest {

	@NotBlank(message = "Refresh token is required")
	private String refreshToken;
}