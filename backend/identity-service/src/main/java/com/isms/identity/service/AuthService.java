package com.isms.identity.service;

import java.time.Instant;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.isms.identity.config.UserMapper;
import com.isms.identity.dto.request.LoginRequest;
import com.isms.identity.dto.request.RefreshTokenRequest;
import com.isms.identity.dto.request.RegisterRequest;
import com.isms.identity.dto.response.LoginResponse;
import com.isms.identity.dto.response.UserResponse;
import com.isms.identity.entity.RefreshToken;
import com.isms.identity.entity.User;
import com.isms.identity.exception.EmailAlreadyExistsException;
import com.isms.identity.exception.UnauthorizedException;
import com.isms.identity.repository.UserRepository;
import com.isms.identity.security.CustomUserDetails;
import com.isms.identity.security.JwtTokenProvider;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final AuthenticationManager authenticationManager;
	private final JwtTokenProvider jwtTokenProvider;
	private final ModelMapper modelMapper;
	private final RefreshTokenService refreshTokenService;

	@Transactional
	public UserResponse register(RegisterRequest request) {
		String email = request.getEmail().trim().toLowerCase();

		if (userRepository.existsByEmail(email)) {
			throw new EmailAlreadyExistsException("Email already registered: " + email);
		}

		User user = buildUserFromRequest(request, email);
		User savedUser = userRepository.save(user);

		return UserMapper.toResponse(savedUser);
	}

	@Transactional
	public LoginResponse login(LoginRequest request, HttpServletRequest httpRequest) {

		User user = userRepository.findByEmail(request.getEmail())
				.orElseThrow(() -> new UnauthorizedException("Invalid email address"));

		try {
			Authentication authentication = authenticationManager
					.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

			CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();

			recordSuccessfulLogin(user);

			String accessToken = jwtTokenProvider.generateAccessToken(principal);
			String refreshToken = refreshTokenService.issueRefreshToken(user.getId(), httpRequest);

			return buildLoginResponse(accessToken, refreshToken, user);

		} catch (BadCredentialsException e) {
			recordFailedLogin(user);
			throw new UnauthorizedException("Invalid email or password");
		} catch (AuthenticationException e) {
			throw new UnauthorizedException(e.getMessage());
		}
	}

	@Transactional
	public LoginResponse refreshToken(RefreshTokenRequest request, HttpServletRequest httpRequest) {
		RefreshToken oldToken = refreshTokenService.validateRefreshToken(request.getRefreshToken());

		User user = userRepository.findById(oldToken.getUserId())
				.orElseThrow(() -> new UnauthorizedException("User not found"));

		String newAccessToken = jwtTokenProvider.generateAccessToken(new CustomUserDetails(user));
		String newRefreshToken = refreshTokenService.rotateRefreshToken(oldToken, httpRequest);

		return buildLoginResponse(newAccessToken, newRefreshToken, user);
	}

	public void logout(String refreshToken) {
		refreshTokenService.logout(refreshToken);
	}

	public void logoutAll(UUID userId) {
		refreshTokenService.logoutAll(userId);
	}

	private User buildUserFromRequest(RegisterRequest request, String normalizedEmail) {
		String phoneNumber = request.getPhoneNumber() != null ? request.getPhoneNumber().trim() : null;

		return User.builder().email(normalizedEmail).passwordHash(passwordEncoder.encode(request.getPassword()))
				.firstName(request.getFirstName().trim()).lastName(request.getLastName().trim())
				.phoneNumber(phoneNumber).passwordChangedAt(Instant.now()).build();
	}

	private LoginResponse buildLoginResponse(String accessToken, String refreshToken, User user) {
		return LoginResponse.builder().accessToken(accessToken).refreshToken(refreshToken)
				.expiresIn(jwtTokenProvider.getAccessTokenExpiration()).user(UserMapper.toResponse(user)).build();
	}

	private void recordSuccessfulLogin(User user) {
		user.resetFailedLoginAttempts();
		user.setLastLoginAt(Instant.now());
		userRepository.save(user);
	}

	private void recordFailedLogin(User user) {
		user.incrementFailedLoginAttempts();
		userRepository.save(user);
	}
}