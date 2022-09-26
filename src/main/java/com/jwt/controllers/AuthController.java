package com.jwt.controllers;

import javax.validation.Valid;

import com.jwt.payload.request.ChangePasswordRequest;
import com.jwt.security.services.AccountControl;
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

  @PostMapping("/signin")
  public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
    return ResponseEntity.ok(accountControl.authenticateUser(loginRequest));
  }

  @PostMapping("/signup")
  public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
    return ResponseEntity.ok(accountControl.registerUser(signUpRequest));
  }

  @PostMapping("/changePassword")
  public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequest changePassword){
    return ResponseEntity.ok(accountControl.changePassword(changePassword));
  }

//  @PostMapping("/resetPassword")
//  public String resetPassword(@RequestBody ChangePasswordRequest resetPassword,
//                              HttpServletRequest request) {
//    User user = userRepository.findByEmail(resetPassword.getEmail());
//    String token = null;
//    if (user != null) {
//
//    }
//  }


}