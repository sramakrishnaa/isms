package com.isms.identity.controller;

import com.isms.identity.dto.request.CreateUserRequest;
import com.isms.identity.dto.request.UpdateUserRequest;
import com.isms.identity.dto.request.UpdateUserStatusRequest;
import com.isms.identity.dto.response.ApiResponse;
import com.isms.identity.dto.response.PaginationResponse;
import com.isms.identity.dto.response.UserResponse;
import com.isms.identity.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

  private final UserService userService;

  @GetMapping
  @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
  public ResponseEntity<ApiResponse<PaginationResponse<UserResponse>>> getUsers(
      @RequestParam(defaultValue = "0") int pageIndex,
      @RequestParam(defaultValue = "5") int pageSize,
      @RequestParam(required = false) String search) {

    PaginationResponse<UserResponse> users = userService.getUsers(pageIndex, pageSize, search);

    ApiResponse<PaginationResponse<UserResponse>> response =
        ApiResponse.success("Users fetched successfully", users);

    return ResponseEntity.ok(response);
  }

  @GetMapping("/{userId}")
  @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
  public ResponseEntity<ApiResponse<UserResponse>> getUser(@PathVariable String userId) {

    UserResponse user = userService.getUser(userId);
    ApiResponse<UserResponse> response = ApiResponse.success("User details success", user);

    return ResponseEntity.ok(response);
  }

  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ApiResponse<String>> createUser(
      @Validated @RequestBody CreateUserRequest createUserRequest) {

    String userId = userService.createUser(createUserRequest);

    ApiResponse<String> response = ApiResponse.success("User created successfully", userId);

    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @PutMapping("/{userId}")
  public ResponseEntity<ApiResponse<String>> updateUser(
      @PathVariable String userId, @Validated @RequestBody UpdateUserRequest updateUserRequest) {
    userService.updateUser(userId, updateUserRequest);
    return ResponseEntity.status(HttpStatus.OK)
        .body(ApiResponse.success("User details updated successfully", null));
  }

  @PatchMapping("/status/{userId}")
  @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
  public ResponseEntity<ApiResponse<Void>> updateUserStaus(
      @PathVariable String userId, @RequestBody UpdateUserStatusRequest statusRequest) {

    userService.updateUserStatus(userId, statusRequest.enabled());
    ApiResponse<Void> response =
        ApiResponse.success(
            statusRequest.enabled() ? "User enabled successfully" : "User disabled successfully",
            null);

    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/{userId}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ApiResponse<String>> deletUser(@PathVariable String userId) {
    userService.deleteUser(userId);
    return ResponseEntity.status(HttpStatus.OK)
        .body(ApiResponse.success("User deleted successfully"));
  }
}
