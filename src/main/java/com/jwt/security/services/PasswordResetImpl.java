package com.jwt.security.services;

import com.jwt.models.User;
import com.jwt.payload.request.ChangePasswordRequest;
import com.jwt.payload.response.ResetPasswordResponse;
import com.jwt.repository.OtpRepository;
import com.jwt.repository.PasswordResetTokenRepository;
import com.jwt.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.Optional;
import java.util.UUID;

@Log4j2
@Service
@RequiredArgsConstructor
@Transactional
public class PasswordResetImpl implements PasswordReset{

    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final UserRepository userRepository;
    private final AccountControl accountControl;
    private final PasswordEncoder encoder;
    private final OtpRepository otpRepository;
    private final JavaMailSender mailSender;

    @Override
    public ResponseEntity<?> resetPassword(@RequestBody ChangePasswordRequest password, HttpServletRequest request) {
        User user = userRepository.findByEmail(password.getEmail());
        String token;
        if (user != null) {
                token = UUID.randomUUID().toString();
                accountControl.createPasswordResetTokenForUser(user, token);
                passwordResetTokenMail(applicationUrl(request), token);
                passwordResetTokenRepository.deleteAll();
                applicationUrl(request);
                return ResponseEntity.ok(new ResetPasswordResponse(token));
        }
//        SimpleMailMessage message = new SimpleMailMessage();
//        try {
//            message.setTo(password.getEmail());
//            message.setSubject("Limited time to 10 minutes. Click the link to Reset your Password: ");
//            message.setText(passwordResetTokenMail(applicationUrl(request), token));
//            mailSender.send(message);
//        } catch (Exception e) {
//            passwordResetTokenRepository.deleteByToken(token);
//            return "Invalid email address or mail server";
//        }
        return ResponseEntity.ok("Wrong email address");
    }

    private void passwordResetTokenMail(String applicationUrl, String token) {
        String url =
                applicationUrl
                        + "/auth/savePassword?token="
                        + token;

        log.info(url);
    }
    private String resetUrl(HttpServletRequest request) {
        return "http://localhost:3000" + request.getContextPath();
    }
    private String applicationUrl(HttpServletRequest request) {
        return "http://" +
                request.getServerName() +
                ":" +
                request.getServerPort() +
                request.getContextPath();
    }

    @Override
    public String savePassword(@Valid @RequestParam("token") String token,
                               @Valid @RequestBody ChangePasswordRequest password){
        String result = accountControl.validatePasswordResetToken(token);
        if(!result.equalsIgnoreCase("valid")){
            return "Invalid Token";
        }
        Optional<User> user = accountControl.getUserByPasswordResetToken(token, new User());
        if(user.isPresent()){
            accountControl.changePassword(user.get(), encoder.encode(password.getNewPassword()));
            passwordResetTokenRepository.deleteByToken(token);
            return "Password Reset Successfully";
        } else {
            return "Invalid Token";
        }
    }

    @Override
    public String saveOtpPassword(@Valid @RequestParam("otp") int otp,
                                  @Valid @RequestBody ChangePasswordRequest password) {
        String result = accountControl.validatePasswordResetOtp(otp);
        if (!result.equalsIgnoreCase("valid")) {
            return "Invalid OTP";
        }
        Optional<User> user = accountControl.getUserByOtp(otp, new User());
        if(user.isPresent()){
            accountControl.changePassword(user.get(), encoder.encode(password.getNewPassword()));
            otpRepository.deleteBy(otp);
            return "Password Reset Successfully";
        } else {
            return "Invalid OTP";
        }
    }
}
