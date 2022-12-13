package com.jwt.payload.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class RefreshTokenRequest {

  @NotBlank
  private String refreshToken;
}
