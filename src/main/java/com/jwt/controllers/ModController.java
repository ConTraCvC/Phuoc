package com.jwt.controllers;

import com.jwt.models.User;
import com.jwt.payload.request.ChangePasswordRequest;
import com.jwt.repository.RefreshTokenRepository;
import com.jwt.repository.UserRepository;
import com.jwt.security.services.AccountControl;
import com.jwt.security.services.PasswordResetImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/auth")
public class ModController extends Thread {
  private final UserRepository userRepository;
  private final PasswordResetImpl passwordReset;
  private final RefreshTokenRepository refreshTokenRepository;
  private final AccountControl accountControl;

  @GetMapping("/mod")
  @PreAuthorize("hasRole('MODERATOR') || hasRole('ADMIN')")
  List<User> getUser() {
    return userRepository.findAll();
  }

  @GetMapping("/user/{id}")
  @PreAuthorize("hasRole('USER') || hasRole('MODERATOR') || hasRole('ADMIN')")
  ResponseEntity<?> getUser(@PathVariable Long id) {
    Optional<User> user = userRepository.findById(id);
    return user.map(response -> ResponseEntity.ok().body(response))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  @DeleteMapping("/admin/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  ResponseEntity<?> deleteUser(@PathVariable Long id, User user ) {
    try {
      Thread thread0 = new Thread(() -> refreshTokenRepository.deleteByTokenId(user));
      synchronized (refreshTokenRepository.deleteByTokenId(user)) {
        thread0.start();
        accountControl.waitForBarrier();
      }
      Thread thread1 = new Thread(() -> userRepository.deleteByUserId(id));
      synchronized (userRepository.deleteByUserId(id)) {
        thread1.start();
      }
    } catch (Exception e) {ResponseEntity.badRequest().body(e.getMessage()); System.out.println(e.getMessage());}
    return ResponseEntity.ok("Successfully");
  }

  @GetMapping(value = "/sendSMS")
  ResponseEntity<?> resetPasswordOTP(@RequestBody ChangePasswordRequest password) {
    return ResponseEntity.ok(passwordReset.resetPasswordOTP(password));
  }

}