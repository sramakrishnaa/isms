package com.isms.identity.util;

import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.isms.identity.exception.UnauthorizedException;
import com.isms.identity.security.CustomUserDetails;

public final class SecurityUtils {

	private SecurityUtils() {
	}

	public static UUID getCurrentUserId() {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || !authentication.isAuthenticated()) {
			throw new UnauthorizedException("Unauthenticated");
		}

		Object principal = authentication.getPrincipal();

		if (principal instanceof CustomUserDetails cud) {
			return cud.getUser().getId();
		}

		if (principal instanceof String username) {
			throw new UnauthorizedException("User details not loaded, cannot resolve " + username);
		}

		throw new UnauthorizedException("Unsupported authentication principal");
	}
}
