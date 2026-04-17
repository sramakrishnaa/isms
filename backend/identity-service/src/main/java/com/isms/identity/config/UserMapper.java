package com.isms.identity.config;

import com.isms.identity.dto.response.UserResponse;
import com.isms.identity.entity.User;
import com.isms.identity.util.MaskingUtils;

public class UserMapper {

	public static UserResponse toResponse(User user) {
		return UserResponse.builder().email(user.getEmail()).firstName(user.getFirstName()).lastName(user.getLastName())
				.maskedPhoneNumber(MaskingUtils.maskPhoneNumber(user.getPhoneNumber()))
				.emailVerified(user.isEmailVerified()).lastLoginAt(user.getLastLoginAt()).build();
	}
}