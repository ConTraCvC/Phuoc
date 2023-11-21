package com.jwt.security.services;

import com.jwt.exception.TokenRefreshException;
import com.jwt.models.*;
import com.jwt.payload.request.*;
import com.jwt.payload.response.JwtResponse;
import com.jwt.payload.response.RefreshTokenResponse;
import com.jwt.payload.response.UserResponse;
import com.jwt.repository.*;
import com.jwt.security.jwt.JwtUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
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

import java.util.*;
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

  private static final Pattern regex = Pattern.compile("(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&_+=()-])(?=\\\\S+$).{8,40}$");
  private static boolean isAlphaBNumeric(String s) {
    return regex.matcher(s).matches();
  }

  @Override
  public ResponseEntity<?> authenticateUser (@Valid @RequestBody User user, HttpServletResponse response) throws ValidationException {
    try {
      Authentication authentication = authenticationManager.authenticate(
              new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));

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
  public ResponseEntity<?> refreshtoken(@Valid @RequestBody RefreshToken request) {
    try{
    return refreshTokenRepository.findByToken(request.getToken())
            .map(refreshTokenService::verifyExpiration)
            .map(RefreshToken::getUser)
            .map(user -> {
              String token = jwtUtils.generateTokenFromUsername(user.getUsername());
              return ResponseEntity.ok(new RefreshTokenResponse(token, request.getToken()));
            })
            .orElseThrow(() -> new TokenRefreshException(request.getToken(),
                    "Refresh token is not exists!"));
    } catch (Exception e) {System.out.println(e.getMessage());}
    return ResponseEntity.badRequest().body("Refresh token is not exists or expired!");
  }

  @Override
  public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) throws ValidationException {

    Optional<User> users = userRepository.findByEmail(signUpRequest.getEmail());
    if (users.isPresent()){
      return ResponseEntity
              .badRequest()
              .body("Error: Username or email is already taken!");
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
        }
      });
    }
    if (isAlphaBNumeric(signUpRequest.getPassword())) {
      userRepository.save(user);
      return ResponseEntity.ok("User registered successfully !");
    } else {
      return ResponseEntity.badRequest().body("Password does not match wellFormed !");
    }
  }

  @Override
  public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequest changePassword){
    Optional<User> user = userRepository.findByEmail(changePassword.getEmail());
    if (user.isEmpty()) {
      return ResponseEntity.badRequest().body("Username not existed");
    }
    if(!bCryptPasswordEncoder.matches(changePassword.getOldPassword(), user.get().getPassword())){
      return ResponseEntity.badRequest().body("Invalid Old Password");
    }
    if (isAlphaBNumeric(changePassword.getNewPassword())) {
    userRepository.changePassword(encoder.encode(changePassword.getNewPassword()), changePassword.getEmail());
    } else {
      return ResponseEntity.badRequest().body("Password does not match wellFormed !");
    }
    return ResponseEntity.ok("Password Changed Successfully");
  }

}