package com.jwt.security.services;

import com.jwt.exception.TokenRefreshException;
import com.jwt.models.*;
import com.jwt.payload.request.*;
import com.jwt.payload.response.JwtResponse;
import com.jwt.payload.response.MessageResponse;
import com.jwt.payload.response.RefreshTokenResponse;
import com.jwt.payload.response.UserResponse;
import com.jwt.repository.*;
import com.jwt.security.jwt.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.*;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountControlImpl implements AccountControl {
  private final JwtUtils jwtUtils;
  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final AuthenticationManager authenticationManager;
  private final PasswordEncoder encoder;
  private final BCryptPasswordEncoder bCryptPasswordEncoder;
  private final RefreshTokenService refreshTokenService;
  private final RefreshTokenRepository refreshTokenRepository;

  @Override
  public ResponseEntity<?> authenticateUser (@Valid @RequestBody LoginRequest loginRequest,
                                             HttpServletResponse response) {
    try {
      Authentication authentication = authenticationManager.authenticate(
              new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

      SecurityContextHolder.getContext().setAuthentication(authentication);
      String jwt = jwtUtils.generateJwtToken(authentication);
      UserResponse userResponse = (UserResponse) authentication.getPrincipal();
      RefreshToken refreshToken = refreshTokenService.createRefreshToken(userResponse.getId());
//      refreshTokenRepository.deleteAllRf();
      if(refreshTokenRepository.findByUserId(userResponse.getId()).isPresent()){
        refreshTokenRepository.updateRefreshToken(refreshToken.getToken(), Date.from(refreshToken.getExpiryDate()), userResponse.getId());}
      else {
        refreshTokenRepository.save(refreshToken);}

//      Cookie cookie = new Cookie("token", jwt);
//      cookie.setHttpOnly(true);
//      cookie.setSecure(true);
//      response.addCookie(cookie);
      List<String> roles = userResponse.getAuthorities().stream()
              .map(GrantedAuthority::getAuthority)
              .collect(Collectors.toList());

      return ResponseEntity.ok(new JwtResponse(jwt,
              refreshToken.getToken(),
              userResponse.getId(),
              userResponse.getUsername(),
              userResponse.getEmail(),
              roles));
    } catch (Exception e) {System.out.println(e.getLocalizedMessage());}
    return ResponseEntity.badRequest().body("Wrong username or password !");
  }

  @Override
  public ResponseEntity<?> refreshtoken(@Valid @RequestBody RefreshTokenRequest request) {
    try{
    String requestRefreshToken = request.getRefreshToken();

    return refreshTokenRepository.findByToken(requestRefreshToken)
            .map(refreshTokenService::verifyExpiration)
            .map(RefreshToken::getUser)
            .map(user -> {
              String token = jwtUtils.generateTokenFromUsername(user.getUsername());
              return ResponseEntity.ok(new RefreshTokenResponse(token, requestRefreshToken));
            })
            .orElseThrow(() -> new TokenRefreshException(requestRefreshToken,
                    "Refresh token is not exists!"));
    } catch (Exception e) {System.out.println(e.getMessage());}
    return ResponseEntity.badRequest().body("Refresh token is not exists!");
  }

  @Override
  public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {

    Optional<User> users = Optional.ofNullable(userRepository.findByEmail(signUpRequest.getEmail()));
    if (users.isPresent()){
      return ResponseEntity
              .badRequest()
              .body(new MessageResponse("Error: Username or email is already in use!"));
    }

    // Create new user's account
    User user = new User(signUpRequest.getUsername(),
            signUpRequest.getEmail(),
            encoder.encode(signUpRequest.getPassword()));
    Set<String> strRoles = signUpRequest.getRole();

    if (strRoles == null) {
      Role userRole = roleRepository.findByRoleCode(ERole.ROLE_USER)
              .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
      user.setRoles(userRole);
    } else {
      strRoles.forEach(role -> {
        switch (role) {
          case "admin" -> {
            Role adminRole = roleRepository.findByRoleCode(ERole.ROLE_ADMIN)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            user.setRoles(adminRole);
          }
          case "mod" -> {
            Role modRole = roleRepository.findByRoleCode(ERole.ROLE_MODERATOR)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            user.setRoles(modRole);
          }
          default -> {
            Role userRole = roleRepository.findByRoleCode(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            user.setRoles(userRole);
          }
        }
      });
    }
    String regex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&_+=()-])(?=\\S+$).{8,40}$";
    Matcher matcher = Pattern.compile(regex).matcher(signUpRequest.getPassword());
    if (matcher.find()) {
      userRepository.save(user);
      return ResponseEntity.ok("User registered successfully !");
    } else {
      return ResponseEntity.badRequest().body("Password does not match wellFormed !");
    }
  }

  @Override
  public String changePassword(@Valid @RequestBody ChangePasswordRequest changePassword){
    User user = userRepository.findByEmail(changePassword.getEmail());
    if (user == null) {
      return "Username not existed";
    }
    if(!bCryptPasswordEncoder.matches(changePassword.getOldPassword(), user.getPassword())){
      return "Invalid Old Password";
    }
    userRepository.changePassword(encoder.encode(changePassword.getNewPassword()), changePassword.getEmail());
    return "Password Changed Successfully";
  }

  @Override
  public void waitForBarrier() {
    final CyclicBarrier barrier = new CyclicBarrier(1);
    try {
      barrier.await();
    } catch (InterruptedException | BrokenBarrierException e) {
      e.printStackTrace();
    }
  }

}