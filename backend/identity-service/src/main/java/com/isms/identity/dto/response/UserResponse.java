package com.isms.identity.dto.response;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
	private String email;
	private String firstName;
	private String lastName;
	private String maskedPhoneNumber;
	private Boolean emailVerified;
	private Instant lastLoginAt;
}
