package com.isms.identity.service;

import java.time.Instant;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
import com.isms.identity.security.CustomUserDetailsService;
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
	private final CustomUserDetailsService customUserDetailsService;

	private void onSuccessfulLogin(User user) {
		user.resetFailedLoginAttempts();
		user.setLastLoginAt(Instant.now());
		userRepository.save(user);
	}

	private void onFailedLogin(User user) {
		user.incrementFailedLoginAttempts();
		userRepository.save(user);
	}

	private String encodePassword(String raw) {
		return passwordEncoder.encode(raw);
	}

	public UserResponse register(RegisterRequest request) {

		String email = request.getEmail().trim().toLowerCase();
		String phoneNumber = request.getPhoneNumber() != null ? request.getPhoneNumber().trim() : null;

		if (userRepository.existsByEmail(email))
			throw new EmailAlreadyExistsException("Email already registered: " + email);

		User user = User.builder().email(email).passwordHash(encodePassword(request.getPassword()))
				.firstName(request.getFirstName().trim()).lastName(request.getLastName().trim())
				.phoneNumber(phoneNumber).passwordChangedAt(Instant.now()).build();

		User savedUser = userRepository.save(user);
		return modelMapper.map(savedUser, UserResponse.class);
	}

	public LoginResponse login(LoginRequest request, HttpServletRequest httpRequest) {

		User user = userRepository.findByEmail(request.getEmail())
				.orElseThrow(() -> new UnauthorizedException("Invaid Email address"));

		try {
			Authentication authentication = authenticationManager
					.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

			CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
			onSuccessfulLogin(user);

			String accessToken = jwtTokenProvider.generateAccessToken(principal);
			String refreshToken = refreshTokenService.issueRefreshToken(user.getId(), httpRequest);

			return LoginResponse.builder().accessToken(accessToken).refreshToken(refreshToken)
					.expiresIn(jwtTokenProvider.getAccessTokenExpiration())
					.user(modelMapper.map(user, UserResponse.class)).build();
		} catch (BadCredentialsException e) {
			onFailedLogin(user);
			throw new UnauthorizedException(e.getMessage());
		} catch (AuthenticationException e) {
			throw new UnauthorizedException(e.getMessage());
		}
	}

	public LoginResponse refreshToken(RefreshTokenRequest request, HttpServletRequest httpRequest) {

		RefreshToken token = refreshTokenService.validateRefreshToken(request.getRefreshToken());

		User user = userRepository.findById(token.getUserId())
				.orElseThrow(() -> new UnauthorizedException("User not found"));

		UserDetails userDetails = customUserDetailsService.loadUserByUsername(user.getEmail());
		String newAccessToken = jwtTokenProvider.generateAccessToken((CustomUserDetails) userDetails);
		String newRefreshToken = refreshTokenService.rotateRefreshToken(request.getRefreshToken(), httpRequest);

		return LoginResponse.builder().accessToken(newAccessToken).refreshToken(newRefreshToken)
				.expiresIn(jwtTokenProvider.getAccessTokenExpiration()).user(modelMapper.map(user, UserResponse.class))
				.build();
	}

	public void logout(String refreshToken) {
		refreshTokenService.logout(refreshToken);
	}

	public void logoutAll(UUID userId) {
		refreshTokenService.logoutAll(userId);
	}

}
