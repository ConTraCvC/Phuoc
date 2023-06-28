package com.jwt.controllers;

import com.jwt.models.CustomerPort;
import com.jwt.models.User;
import com.jwt.payload.request.ChangePasswordRequest;
import com.jwt.repository.UserRepository;
import com.jwt.security.server.PortService;
import com.jwt.security.server.ServerPortHandle;
import com.jwt.security.services.PasswordReset;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.LifecycleException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/auth")
public class ModController extends Thread {
  private final UserRepository userRepository;
  private final PasswordReset passwordReset;
  private final PortService portService;

  @GetMapping("/mod")
  @PreAuthorize("hasRole('MODERATOR') || hasRole('ADMIN')")
  List<User> getUser() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    // Reflection test
//    Method test = Test.class.getDeclaredMethod("reflection", int.class, int.class);
//    test.setAccessible(true); // Access private methods
//    Test test1 = new Test();
//    int tests = (Integer) test.invoke(test1, 67, 99);
//    System.out.println(tests);
    return userRepository.findAllUser();
  }

  @GetMapping("/user/{id}")
  @PreAuthorize("hasRole('USER') || hasRole('MODERATOR') || hasRole('ADMIN')")
  ResponseEntity<?> getUser(@PathVariable Long id) {
    Optional<User> user = userRepository.findById(id);
    return user.map(response -> ResponseEntity.ok().body(response))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  @GetMapping(value = "/sendSMS")
  ResponseEntity<?> resetPasswordOTP(@RequestBody ChangePasswordRequest password) {
    return ResponseEntity.ok(passwordReset.resetPasswordOTP(password));
  }

  @PostMapping(value = "/createPort")
  @PreAuthorize("hasRole('ADMIN')")
  ResponseEntity<?> createPort(@RequestBody CustomerPort port, HttpServletRequest request) throws LifecycleException {
    return ResponseEntity.ok(portService.portRegister(port, request));
  }

  @DeleteMapping(value = "/deletePort")
  @PreAuthorize("hasRole('ADMIN')")
  ResponseEntity<?> deletePort(@RequestBody CustomerPort port, HttpServletRequest request) throws LifecycleException {
    return ResponseEntity.ok(portService.deletePort(port, request));
  }
}