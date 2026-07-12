package com.isms.identity.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssignRoleRequest {

  @NotBlank(message = "User ID is required")
  private String userId;

  @NotBlank(message = "Role name is required")
  private String roleName;
}
