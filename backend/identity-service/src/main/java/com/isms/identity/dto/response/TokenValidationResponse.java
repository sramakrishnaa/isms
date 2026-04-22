package com.isms.identity.dto.response;

import java.util.List;
import java.util.UUID;

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
public class TokenValidationResponse {
	private boolean valid;
	private UUID userId;
	private UUID tenantId;
	private List<String> roles;
	private List<String> permissions;

	public static TokenValidationResponse invalid() {
		return TokenValidationResponse.builder().valid(false).build();
	}
}