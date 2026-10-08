package com.isms.identity.dto.request;

public enum RequiredUserAction {

	VERIFY_EMAIL("VERIFY_EMAIL"), UPDATE_PASSWORD("UPDATE_PASSWORD"), UPDATE_PROFILE("UPDATE_PROFILE");

	private final String keycloakValue;

	RequiredUserAction(String keycloakValue) {
		this.keycloakValue = keycloakValue;
	}

	public String getKeycloakValue() {
		return keycloakValue;
	}
}