package com.isms.identity.dto.response;

import java.time.Instant;
import java.util.UUID;

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
public class UserResponse{

	private UUID id;
    private String email;
    private String username;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private Boolean emailVerified;
    private Boolean active;
    private Instant lastLoginAt;
    private Instant createdAt;
}
