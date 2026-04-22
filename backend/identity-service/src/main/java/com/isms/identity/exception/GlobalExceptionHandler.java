package com.isms.identity.exception;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import com.isms.identity.dto.response.ApiResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(UnauthorizedException.class)
	public ResponseEntity<ApiResponse<Void>> handleUnauthorizedException(UnauthorizedException ex) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error(ex.getMessage()));
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ApiResponse<Void>> handleResourceNotFoundException(ResourceNotFoundException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(ex.getMessage()));
	}

	@ExceptionHandler(EmailAlreadyExistsException.class)
	public ResponseEntity<ApiResponse<Void>> handleEmailAlreadyExistsException(EmailAlreadyExistsException ex) {
		return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.error(ex.getMessage()));
	}

	@ExceptionHandler(InvalidTokenException.class)
	public ResponseEntity<ApiResponse<Void>> handleInvalidTokenException(InvalidTokenException ex) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error(ex.getMessage()));
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ApiResponse<Void>> handleAccessDeniedException(AccessDeniedException ex) {
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.error("Access denied"));
	}

	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<Object> handleUserNotFoundException(UserNotFoundException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(ex.getMessage()));
	}

	@ExceptionHandler(AuthenticationException.class)
	public ResponseEntity<Object> handleAuthenticationException(AuthenticationException ex, WebRequest request) {
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.error(ex.getMessage()));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiResponse<Map<String, List<String>>>> handleMethodArgumentNotValidException(
			MethodArgumentNotValidException ex) {

		Map<String, List<String>> errors = ex.getBindingResult().getFieldErrors().stream()
				.collect(Collectors.groupingBy(FieldError::getField,
						Collectors.mapping(FieldError::getDefaultMessage, Collectors.toList())));
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.<Map<String, List<String>>>builder()
				.success(false).message("Validation failed").data(errors).build());
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<ApiResponse<String>> handleDataIntegrityViolationException(
			DataIntegrityViolationException ex) {
		return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.error("Duplicate or Invalid Data"));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception ex) {
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(ApiResponse.error("An unexpected error occurred"));
	}

}
