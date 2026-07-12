package com.isms.identity.exception;

import com.isms.identity.dto.response.ErrorResponse;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(UserNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleUserNotFound(
      UserNotFoundException ex, WebRequest request) {
    log.warn("UserNotFoundException: {}", ex.getMessage());
    return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request);
  }

  @ExceptionHandler(UserAlreadyExistsException.class)
  public ResponseEntity<ErrorResponse> handleUserAlreadyExists(
      UserAlreadyExistsException ex, WebRequest request) {
    log.warn("UserAlreadyExistsException: {}", ex.getMessage());
    return buildResponse(HttpStatus.CONFLICT, ex.getMessage(), request);
  }

  @ExceptionHandler(KeycloakOperationException.class)
  public ResponseEntity<ErrorResponse> handleKeycloakException(
      KeycloakOperationException ex, WebRequest request) {
    log.error("KeycloakOperationException: {}", ex.getMessage(), ex);
    return buildResponse(HttpStatus.BAD_GATEWAY, ex.getMessage(), request);
  }

  @ExceptionHandler(InvalidRequestException.class)
  public ResponseEntity<ErrorResponse> handleInvalidRequest(
      InvalidRequestException ex, WebRequest request) {
    log.warn("InvalidRequestException: {}", ex.getMessage());
    return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidationException(
      MethodArgumentNotValidException ex, WebRequest request) {
    Map<String, String> errors = new LinkedHashMap<>();
    ex.getBindingResult()
        .getFieldErrors()
        .forEach(e -> errors.put(e.getField(), e.getDefaultMessage()));
    log.warn("Validation failed: {}", errors);
    return buildResponse(HttpStatus.BAD_REQUEST, "Invalid request data", request, errors);
  }

  @ExceptionHandler({AuthorizationDeniedException.class, AccessDeniedException.class})
  public ResponseEntity<ErrorResponse> handleAccessDenied(Exception ex, WebRequest request) {
    log.warn("Access denied on {}: {}", request.getDescription(false), ex.getMessage());
    return buildResponse(HttpStatus.FORBIDDEN, "Access denied: insufficient permissions", request);
  }

  @ExceptionHandler(NoHandlerFoundException.class)
  public ResponseEntity<ErrorResponse> handleNoHandlerFound(
      NoHandlerFoundException ex, WebRequest request) {
    log.warn("No handler found: {}", request.getDescription(false));
    return buildResponse(HttpStatus.NOT_FOUND, "Endpoint not found " + ex.getRequestURL(), request);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, WebRequest request) {
    log.error("Unhandled exception on {}: {}", request.getDescription(false), ex.getMessage(), ex);
    return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong", request);
  }

  private ResponseEntity<ErrorResponse> buildResponse(
      HttpStatus status, String message, WebRequest request) {
    return buildResponse(status, message, request, null);
  }

  private ResponseEntity<ErrorResponse> buildResponse(
      HttpStatus status, String message, WebRequest request, Map<String, String> validationErrors) {

    ErrorResponse response =
        ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(status.value())
            .error(status.getReasonPhrase())
            .message(message)
            .path(getRequestPath(request))
            .validationErrors(validationErrors)
            .build();

    return ResponseEntity.status(status).body(response);
  }

  private String getRequestPath(WebRequest request) {
    return request.getDescription(false).replace("uri=", "");
  }
}
