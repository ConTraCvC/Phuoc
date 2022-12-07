package com.jwt.payload.response;

import lombok.Data;

@Data
public class ResetPasswordResponse {
  private String rsToken;

  public ResetPasswordResponse(String token) {
    this.rsToken = token;
  }
}
