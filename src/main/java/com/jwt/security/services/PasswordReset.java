package com.jwt.security.services;

import com.jwt.models.PasswordResetToken;
import com.jwt.payload.request.ChangePasswordRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

public interface PasswordReset {

   ResponseEntity<?> resetPassword(@RequestBody ChangePasswordRequest passwordModel, HttpServletRequest request, PasswordResetToken resetToken);

    String savePassword(@RequestParam("token") String token,
                        @RequestBody ChangePasswordRequest password);

    String saveOtpPassword(@RequestParam("otp") int otp,
                           @RequestBody ChangePasswordRequest password);
}
