package com.isms.identity.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateUserRequest {

	@NotBlank(message = "Email is required")
	@Email(message = "Invalid email format")
	@Size(max = 255, message = "Email cannot exceed 255 characters")
	private String email;

	@NotBlank(message = "First name is required")
	@Size(min = 2, max = 100, message = "First name must be between 2 and 100 characters")
	private String firstName;

	@NotBlank(message = "Last name is required")
	@Size(min = 2, max = 100, message = "Last name must be between 2 and 100 characters")
	private String lastName;

	private boolean enabled = true;

	private boolean emailVerified = false;

	private List<RequiredUserAction> requiredActions;
}
