package com.isms.identity.dto.response;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenResponse {

	private LocalDateTime timestamp;
	private String access_token;

	public TokenResponse(String access_token) {
		this.access_token = access_token;
		this.timestamp = LocalDateTime.now();
	}

}
