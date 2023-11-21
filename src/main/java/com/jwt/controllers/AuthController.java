package com.jwt.controllers;

import com.jwt.models.PasswordResetToken;
import com.jwt.models.RefreshToken;
import com.jwt.models.User;
import com.jwt.payload.request.ChangePasswordRequest;
import com.jwt.security.services.AccountControl;
import com.jwt.security.services.PasswordReset;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.jwt.payload.request.SignupRequest;

@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/auth")
public class AuthController {
  private final AccountControl accountControl;
  private final PasswordReset passwordReset;

  @PostMapping("/sign-in")
  ResponseEntity<?> authenticateUser(@RequestBody User user, HttpServletResponse response) {
    return ResponseEntity.ok(accountControl.authenticateUser(user, response));
  }

  @PostMapping("/sign-up")
  ResponseEntity<?> registerUser(@RequestBody SignupRequest signUpRequest) {
    return ResponseEntity.ok(accountControl.registerUser(signUpRequest));
  }

  @PostMapping("/refreshToken")
  ResponseEntity<?> refreshToken(@RequestBody RefreshToken request) {
    return ResponseEntity.ok(accountControl.refreshtoken(request));
  }

  @PostMapping("/changePassword")
  ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest changePassword) {
    return ResponseEntity.ok(accountControl.changePassword(changePassword));
  }

  @PostMapping("/resetPassword")
  ResponseEntity<?> resetPassword(@RequestBody ChangePasswordRequest resetPassword,
                                  HttpServletRequest request, PasswordResetToken tokenRS) {
    return ResponseEntity.ok(passwordReset.resetPassword(resetPassword, request, tokenRS));
  }

  @PostMapping("/savePassword")
  ResponseEntity<String> savePassword(@RequestParam("token") String token,
                                      @RequestBody ChangePasswordRequest savePassword) throws InterruptedException {
    return ResponseEntity.ok(passwordReset.savePassword(token, savePassword));
  }

  @PostMapping("/otp")
  ResponseEntity<?> saveOtpPassword(@RequestParam("otp") int otp,
                                    @RequestBody ChangePasswordRequest savePassword) throws InterruptedException {
    return ResponseEntity.ok(passwordReset.saveOtpPassword(otp, savePassword));
  }

}