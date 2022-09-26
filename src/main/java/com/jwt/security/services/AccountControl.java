package com.jwt.security.services;

import com.jwt.payload.request.ChangePasswordRequest;
import com.jwt.payload.request.LoginRequest;
import com.jwt.payload.request.SignupRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

public interface AccountControl {
    ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest);

    ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest);

    String changePassword(@Valid @RequestBody ChangePasswordRequest changePassword);
}
