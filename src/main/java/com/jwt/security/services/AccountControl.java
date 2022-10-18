package com.jwt.security.services;

import com.jwt.models.User;
import com.jwt.payload.request.ChangePasswordRequest;
import com.jwt.payload.request.LoginRequest;
import com.jwt.payload.request.SignupRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.Optional;

public interface AccountControl {
    ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest);

    ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest);

    String changePassword(@Valid @RequestBody ChangePasswordRequest changePassword);

    String validatePasswordResetToken(String token);
    String validatePasswordResetOtp(int otp);

    void createPasswordResetTokenForUser(User user, String token);

    Optional<User> getUserByPasswordResetToken(String token, User user);

    void changePassword(User user, String newPassword);

    void createPasswordResetOtp(User user, int otp);

    Optional<User> getUserByOtp(int otp, User user);
}
