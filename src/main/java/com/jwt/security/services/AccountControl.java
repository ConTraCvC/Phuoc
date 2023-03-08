package com.jwt.security.services;

import com.jwt.models.RefreshToken;
import com.jwt.models.User;
import com.jwt.payload.request.ChangePasswordRequest;
import com.jwt.payload.request.SignupRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

public interface AccountControl {
  ResponseEntity<?> authenticateUser(@Valid @RequestBody User user, HttpServletResponse response);

  ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest);

  ResponseEntity<?> refreshtoken(@Valid @RequestBody RefreshToken request);

  ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequest changePassword);

}
