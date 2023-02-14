package com.jwt.security.services;

import com.jwt.models.User;
import com.jwt.payload.request.ChangePasswordRequest;
import com.jwt.payload.request.LoginRequest;
import com.jwt.payload.request.RefreshTokenRequest;
import com.jwt.payload.request.SignupRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

public interface AccountControl {
  ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest, HttpServletResponse response);

  ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest);

  ResponseEntity<?> refreshtoken(@Valid @RequestBody RefreshTokenRequest request);

  String changePassword(@Valid @RequestBody ChangePasswordRequest changePassword);

  void waitForBarrier();

}
