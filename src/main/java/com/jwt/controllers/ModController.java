package com.jwt.controllers;

import com.jwt.models.User;
import com.jwt.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/rest")
public class ModController {

  private final Logger log = LoggerFactory.getLogger(AuthController.class);
  private final UserRepository userRepository;

  @GetMapping("/mod")
  @PreAuthorize("hasRole('MODERATOR') || hasRole('ADMIN')")
  List<User> getUser() {
    return userRepository.findAll();
  }

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
}