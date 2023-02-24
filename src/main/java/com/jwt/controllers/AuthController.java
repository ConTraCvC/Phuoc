package com.jwt.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import com.jwt.models.PasswordResetToken;
import com.jwt.models.RefreshToken;
import com.jwt.models.User;
import com.jwt.payload.request.ChangePasswordRequest;
import com.jwt.security.services.AccountControl;
import com.jwt.security.services.PasswordReset;
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
  ResponseEntity<?> authenticateUser(@Valid @RequestBody User user, HttpServletResponse response) {
    return ResponseEntity.ok(accountControl.authenticateUser(user, response));
  }

  @PostMapping("/sign-up")
  ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
    return ResponseEntity.ok(accountControl.registerUser(signUpRequest));
  }

  @PostMapping("/changePassword")
  ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequest changePassword) {
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

  @PostMapping("/refreshToken")
  ResponseEntity<?> refreshtoken(@Valid @RequestBody RefreshToken request) {
    return ResponseEntity.ok(accountControl.refreshtoken(request));
  }

  @PostMapping("/otp")
  ResponseEntity<?> saveOtpPassword(@RequestParam("otp") int otp,
                                    @RequestBody ChangePasswordRequest savePassword) throws InterruptedException {
    return ResponseEntity.ok(passwordReset.saveOtpPassword(otp, savePassword));
  }

}