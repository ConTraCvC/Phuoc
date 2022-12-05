package com.jwt.payload.response;

import lombok.Data;

@Data
public class ResetPasswordResponse {
  private String token;

  public ResetPasswordResponse(String token) {
    this.token = token;
  }
}
