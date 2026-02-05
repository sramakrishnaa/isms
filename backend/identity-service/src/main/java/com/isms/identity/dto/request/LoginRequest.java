package com.isms.identity.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoginRequest {

	@NotNull(message = "Email is required")
	@NotBlank(message = "Email cannot be blank")
	@Email(message = "Please provide a valid email address")
	private String email;

	@NotNull(message = "Password is required")
	@NotBlank(message = "Password cannot be blank")
	private String password;
}
