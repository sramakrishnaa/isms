package com.isms.identity.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailsResponse {

	private String id;
	private String username;
	private String firstName;
	private String lastName;
	private String email;
	private boolean emailVerified;
	private boolean enabled;
	private Long createdTimestamp;
	private List<String> realmRoles;

}