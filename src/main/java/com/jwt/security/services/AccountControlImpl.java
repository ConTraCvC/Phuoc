package com.jwt.security.services;

import com.jwt.models.*;
import com.jwt.payload.request.ChangePasswordRequest;
import com.jwt.payload.request.LoginRequest;
import com.jwt.payload.request.Otp;
import com.jwt.payload.request.SignupRequest;
import com.jwt.payload.response.JwtResponse;
import com.jwt.payload.response.MessageResponse;
import com.jwt.repository.OtpRepository;
import com.jwt.repository.PasswordResetTokenRepository;
import com.jwt.repository.RoleRepository;
import com.jwt.repository.UserRepository;
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

import javax.validation.Valid;
import java.util.*;
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
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final OtpRepository otpRepository;

    @Override
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles));
    }

    @Override
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {

        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }

        // Create new user's account
        User user = new User(signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()));

        Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role userRole = roleRepository.findByRoleCode(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin" -> {
                        Role adminRole = roleRepository.findByRoleCode(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);
                    }
                    case "mod" -> {
                        Role modRole = roleRepository.findByRoleCode(ERole.ROLE_MODERATOR)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(modRole);
                    }
                    default -> {
                        Role userRole = roleRepository.findByRoleCode(ERole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(userRole);
                    }
                }
            });
        }

        user.setRoles(roles);
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));

    }

    @Override
    public String changePassword(@Valid @RequestBody ChangePasswordRequest changePassword){
        User user = userRepository.findByEmail(changePassword.getEmail());
        if (user == null) {
            return ("Username not existed");
        }
        if(!bCryptPasswordEncoder.matches(changePassword.getOldPassword(), user.getPassword())){
            return "Invalid Old Password";
        }
        userRepository.changePassword(encoder.encode(changePassword.getNewPassword()), changePassword.getEmail());
        return "Password Changed Successfully";
    }

    @Override
    public String validatePasswordResetToken(String token) {
        PasswordResetToken passwordResetToken
                = passwordResetTokenRepository.findByToken(token);
        if (passwordResetToken == null) {
            return "Invalid";
        }
        Calendar cal = Calendar.getInstance();

        if ((passwordResetToken.getExpirationTime().getTime()
                - cal.getTime().getTime()) <= 0) {
            passwordResetTokenRepository.delete(passwordResetToken);
            return "Expired";
        }
        return "Valid";
    }

    @Override
    public String validatePasswordResetOtp(int otp) {
        Otp otpCode = otpRepository.findByOtp(otp);
        if (otpCode == null) {
            return "Invalid";
        }
        Calendar cal = Calendar.getInstance();

        if((otpCode.getRealTime().getTime() - cal.getTime().getTime()) <=0){
            otpRepository.delete(otpCode);
            return "Expired";
        }
        return "Valid";
    }

    @Override
    public void createPasswordResetTokenForUser(User user, String token) {
        PasswordResetToken passwordResetToken
                = new PasswordResetToken(token, user);
        passwordResetTokenRepository.save(passwordResetToken);
    }

    @Override
    public void createPasswordResetOtp(User user, int otp) {
        Otp otpCode = new Otp(user, otp);
        otpRepository.save(otpCode);
    }

    @Override
    public Optional<User> getUserByPasswordResetToken(String token, User user) {
        return Optional.ofNullable(passwordResetTokenRepository.findByToken(token).getUser());
    }

    @Override
    public void changePassword(User user, String newPassword) {
        user.setPassword(newPassword);
        userRepository.save(user);
    }

    @Override
    public Optional<User> getUserByOtp(int otp, User user) {
        return Optional.ofNullable(otpRepository.findByOtp(otp).getUser());
    }

}