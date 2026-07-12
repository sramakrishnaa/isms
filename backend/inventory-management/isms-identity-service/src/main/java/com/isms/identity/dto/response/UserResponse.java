package com.isms.identity.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
  private String id;
  private String username;
  private String email;
  private String firstName;
  private String lastName;
  private boolean enabled;
  private boolean emailVerified;
  private Long createdTimestamp;
  private List<String> roles;
}
