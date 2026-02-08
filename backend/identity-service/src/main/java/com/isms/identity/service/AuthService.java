package com.isms.identity.service;

import java.time.Instant;

import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
import com.isms.identity.security.JwtTokenProvider;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional
public class AuthService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final AuthenticationManager authenticationManager;
	private final JwtTokenProvider jwtTokenProvider;
	private final ModelMapper modelMapper;
	private final RefreshTokenService refreshTokenService;
	private final UserDetailsService userDetailsService;

	public UserResponse register(RegisterRequest request) {

		String email = request.getEmail().trim().toLowerCase();
		String username = request.getUsername().trim();
		String phoneNumber = request.getPhoneNumber() != null ? request.getPhoneNumber().trim() : null;

		if (userRepository.existsByEmail(email)) {
			throw new EmailAlreadyExistsException("Email already registered: " + email);
		}

		if (userRepository.existsByUsername(username)) {
			throw new EmailAlreadyExistsException("Username already taken: " + username);
		}

		User user = User.builder().email(email).username(username)
				.passwordHash(passwordEncoder.encode(request.getPassword())).firstName(request.getFirstName().trim())
				.lastName(request.getLastName()).phoneNumber(phoneNumber.trim()).passwordChangedAt(Instant.now())
				.build();

		User savedUser = userRepository.save(user);
		return modelMapper.map(savedUser, UserResponse.class);
	}

	public LoginResponse login(LoginRequest request, HttpServletRequest httpRequest) {

		String email = request.getEmail().trim();

		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new UnauthorizedException("Invalid email or password"));
		if (!user.isEnabled()) {
			throw new UnauthorizedException("Account is disabled");
		}

		if (!user.isAccountNonLocked()) {
			throw new UnauthorizedException("Account is locked due to multiple failed login attempts");
		}

		try {
			Authentication authentication = authenticationManager
					.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

			handleSuccessfulLogin(user);
			
			UserDetails userDetails = (UserDetails) authentication.getPrincipal();
			String accessToken = jwtTokenProvider.generateToken(userDetails);
			String refreshToken = refreshTokenService.createRefreshToken(user.getId(), httpRequest);

			return LoginResponse.builder().accessToken(accessToken).refreshToken(refreshToken)
					.expiresIn(jwtTokenProvider.getAccessTokenExpiration())
					.user(modelMapper.map(user, UserResponse.class)).build();
		} catch (Exception e) {
			handleFailedLogin(user);
			throw new UnauthorizedException("Invalid email or password");
		}
	}

	public LoginResponse refreshToken(RefreshTokenRequest request, HttpServletRequest httpRequest) {

		RefreshToken token = refreshTokenService.validateRefreshToken(request.getRefreshToken());

		User user = userRepository.findById(token.getUserId())
				.orElseThrow(() -> new UnauthorizedException("User not found"));

		UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
		String newAccessToken = jwtTokenProvider.generateToken(userDetails);
		String newRefreshToken = refreshTokenService.rotateRefreshToken(request.getRefreshToken(), httpRequest);

		return LoginResponse.builder().accessToken(newAccessToken).refreshToken(newRefreshToken)
				.expiresIn(jwtTokenProvider.getAccessTokenExpiration()).user(modelMapper.map(user, UserResponse.class))
				.build();
	}

	private void handleFailedLogin(User user) {
		user.incrementFailedLoginAttempts();
		userRepository.save(user);
	}

	private void handleSuccessfulLogin(User user) {
		user.resetFailedLoginAttempts();
		user.setLastLoginAt(Instant.now());
		userRepository.save(user);
	}

}
