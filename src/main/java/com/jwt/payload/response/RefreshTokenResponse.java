package com.jwt.payload.response;

import lombok.Data;

@Data
public class RefreshTokenResponse {

  private String toke;
  private String refreshToken;
  private String tokenType = "Bearer";

  public RefreshTokenResponse(String token, String refreshToken) {
    this.token = token;
    this.refreshToken = refreshToken;
  }
}