package com.jwt.payload.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ChangePasswordRequest {
  @NotBlank
  private String email;
  @NotBlank
  private String newPassword;
  @NotBlank
  private String oldPassword;
}