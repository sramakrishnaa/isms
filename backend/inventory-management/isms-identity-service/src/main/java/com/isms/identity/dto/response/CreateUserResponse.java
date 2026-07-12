package com.isms.identity.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreateUserResponse {

  private String id;
  private String message;
}
