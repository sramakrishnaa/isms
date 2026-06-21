package com.isms.identity.util;

import java.util.List;

import org.keycloak.representations.idm.RoleRepresentation;

public class KeycloakRoleUtils {

    public static List<String> getBusinessRoles(
            List<RoleRepresentation> roles) {

        return roles.stream()
                .map(RoleRepresentation::getName)
                .filter(role -> role.startsWith("ISMS_"))
                .toList();
    }

}