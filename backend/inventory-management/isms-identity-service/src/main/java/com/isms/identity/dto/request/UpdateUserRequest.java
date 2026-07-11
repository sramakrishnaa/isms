package com.isms.identity.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserRequest {

	@Email(message = "Must be a valid email address")
	private String email;

	@Size(min = 1, max = 50, message = "First name must be between 1 and 50 characters")
	private String firstName;

	@Size(min = 1, max = 50, message = "Last name must be between 1 and 50 characters")
	private String lastName;

}