package com.jwt.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.jwt.payload.request.ChangePasswordRequest;
import com.jwt.security.services.AccountControl;
import com.jwt.security.services.PasswordReset;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.jwt.payload.request.LoginRequest;
import com.jwt.payload.request.SignupRequest;
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/auth")
public class AuthController {
  private final AccountControl accountControl;
  private final PasswordReset passwordReset;

  @PostMapping("/sign-in")
  public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
    return ResponseEntity.ok(accountControl.authenticateUser(loginRequest));
  }

  @PostMapping("/sign-up")
  public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
    return ResponseEntity.ok(accountControl.registerUser(signUpRequest));
  }

  @PostMapping("/changePassword")
  public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequest changePassword) {
    return ResponseEntity.ok(accountControl.changePassword(changePassword));
  }

  @PostMapping("/resetPassword")
  public ResponseEntity<String> resetPassword(@RequestBody ChangePasswordRequest resetPassword,
                                              HttpServletRequest request) {
    return ResponseEntity.ok(passwordReset.resetPassword(resetPassword, request));
  }

  @PostMapping("/savePassword")
  public ResponseEntity<String> savePassword(@RequestParam("token") String token,
                                             @RequestBody ChangePasswordRequest savePassword) {
    return ResponseEntity.ok(passwordReset.savePassword(token, savePassword));
  }

  @PostMapping("/otp")
  public ResponseEntity<?> saveOtpPassword(@RequestParam("otp") int otp,
                                           @RequestBody ChangePasswordRequest savePassword) {
    return ResponseEntity.ok(passwordReset.saveOtpPassword(otp, savePassword));
  }

}