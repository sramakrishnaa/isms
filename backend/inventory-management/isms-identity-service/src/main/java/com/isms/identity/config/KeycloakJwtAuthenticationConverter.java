package com.isms.identity.config;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

public class KeycloakJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {
	@Value("${keycloak.client-id}")
	private String clientId;

	@Override
	public AbstractAuthenticationToken convert(@NonNull Jwt source) {

		return new JwtAuthenticationToken(source,
				Stream.concat(new JwtGrantedAuthoritiesConverter().convert(source).stream(),
						extractResourceRoles(source).stream()).collect(Collectors.toSet()));
	}

	private Collection<? extends GrantedAuthority> extractResourceRoles(Jwt jwt) {

		Map<String, Object> resourceAccess = jwt.getClaim("resource_access");

		if (resourceAccess == null)
			return List.of();

		Map<String, Object> client = (Map<String, Object>) resourceAccess.get(clientId);

		if (client == null)
			return List.of();

		List<String> roles = (List<String>) client.get("roles");
		if (roles == null)
			return List.of();

		return roles.stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role)).collect(Collectors.toSet());
	}
}