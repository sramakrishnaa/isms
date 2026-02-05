package com.isms.identity.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {

	public static final String PASSWORD_REGEX = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#$%!]).{8,20}$";

	@NotNull(message = "Email is required")
	@NotBlank(message = "Email cannot be blank")
	@Email(message = "Please provide a valid email address")
	private String email;

	@NotNull(message = "Name is required")
	@NotBlank(message = "Name cannot be blank")
	@Size(min = 2, max = 50)
	private String name;

	@NotNull(message = "Phone number is required")
	@NotBlank(message = "Phone number cannot be blank")
	@Pattern(regexp = "^[6-9][0-9]{9}$", message = "Phone number must be a valid 10-digit mobile number")
	private String phoneNumber;

	@NotNull(message = "Password is required")
	@NotBlank(message = "Password cannot be blank")
	@Size(min = 8, max = 20)
	@Pattern(regexp = PASSWORD_REGEX, message = "Password must contain uppercase, lowercase, number, and special character")
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private String password;

}
