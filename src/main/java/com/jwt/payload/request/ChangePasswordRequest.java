package com.jwt.payload.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChangePasswordRequest {
  @NotBlank
  private String email;
  @NotBlank
  private String newPassword;
  @NotBlank
  private String oldPassword;
}