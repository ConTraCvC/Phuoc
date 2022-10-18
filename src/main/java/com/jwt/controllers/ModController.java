package com.jwt.controllers;

import com.jwt.models.User;
import com.jwt.payload.request.ChangePasswordRequest;
import com.jwt.repository.UserRepository;
import com.jwt.security.services.AccountControl;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/rest")
public class ModController {

  private final Logger log = LoggerFactory.getLogger(AuthController.class);
  private final UserRepository userRepository;
  private final AccountControl accountControl;

  @GetMapping("/mod")
  @PreAuthorize("hasRole('MODERATOR') || hasRole('ADMIN')")
  List<User> getUser() {
    return userRepository.findAll();
  }

//  @GetMapping("/user")
//  ResponseEntity<User> getUser(String username) {
//    Optional<User> user = userRepository.findByUsername(username);
//    return user.map(response -> ResponseEntity.ok().body(response))
//            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
//  }

  @GetMapping("/user/{id}")
  @PreAuthorize("hasRole('MODERATOR') || hasRole('ADMIN')")
  ResponseEntity<?> getUser(@PathVariable Long id) {
    Optional<User> user = userRepository.findById(id);
    return user.map(response -> ResponseEntity.ok().body(response))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  @DeleteMapping("/admin/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  ResponseEntity<?> deleteUser(@PathVariable Long id) {
    log.info("Request to delete group: {}", id);
    userRepository.deleteById(id);
    return ResponseEntity.ok().build();
  }

  @GetMapping(value = "/sendSMS")
  public ResponseEntity<?> saveOtpPassword(@RequestBody ChangePasswordRequest password) {

    Twilio.init("AC428df5bd302a88e1e314d9ece0159181", "39a084170d74b89e3d96382d2311d784");
    User user = userRepository.findByEmail(password.getEmail());
    Random r = new Random();
    int otpCode = 100000 + r.nextInt(800000);
    accountControl.createPasswordResetOtp(user, otpCode);
    Message.creator(new PhoneNumber("+84866682422"),
            new PhoneNumber("+19497495157"),
            "Limited OTP code to 10 minutes: " + otpCode).create();
    return ResponseEntity.ok("OTP Send Successfully");
  }

}