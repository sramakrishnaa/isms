package com.isms.identity.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
	private String accessToken;
	private String refreshToken;
	@Builder.Default
	private String tokenType = "Bearer";
	private Long expiresIn;
	private UserResponse user;
}