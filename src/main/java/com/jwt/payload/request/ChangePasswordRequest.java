package com.jwt.payload.request;

import lombok.Data;

@Data
public class ChangePasswordRequest {
    private String username;
    private String newPassword;
    private String currentPassword;
}