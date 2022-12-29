package com.jwt.payload.response;

import lombok.Data;

@Data
public class RefreshTokenResponse {

  private String token;
  private String refreshToken;
  private String tokenType = "Bearer";

  public RefreshTokenResponse(String token, String refreshToken) {
    this.token = token;
    this.refreshToken = refreshToken;
  }
}