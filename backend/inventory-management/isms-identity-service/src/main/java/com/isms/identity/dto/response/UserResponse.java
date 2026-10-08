package com.isms.identity.dto.response;

import com.isms.identity.dto.request.RequiredUserAction;
import lombok.*;

import java.util.List;

import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.MappingsRepresentation;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
	private String id;
	private String username;
	private String email;
	private String firstName;
	private String lastName;
	private boolean enabled;
	private boolean emailVerified;
	private Long createdTimestamp;
	private List<RequiredUserAction> requiredActions;
	private MappingsRepresentation roles;
	private List<CredentialRepresentation> credentials;
}
