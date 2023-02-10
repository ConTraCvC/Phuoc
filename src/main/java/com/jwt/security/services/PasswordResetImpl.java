package com.jwt.security.services;

import com.jwt.models.PasswordResetToken;
import com.jwt.models.User;
import com.jwt.payload.request.ChangePasswordRequest;
import com.jwt.models.Otp;
import com.jwt.payload.response.ResetPasswordResponse;
import com.jwt.repository.OtpRepository;
import com.jwt.repository.PasswordResetTokenRepository;
import com.jwt.repository.UserRepository;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
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
import java.util.Calendar;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log4j2
@Service
@RequiredArgsConstructor
@Transactional
public class PasswordResetImpl implements PasswordReset{

  private final PasswordResetTokenRepository passwordResetTokenRepository;
  private final UserRepository userRepository;
  private final PasswordEncoder encoder;
  private final OtpRepository otpRepository;
  private final JavaMailSender mailSender;

  public Optional<User> getUserByPasswordResetToken(String token, User user) {
    return Optional.of(passwordResetTokenRepository.findByToken(token).getUser());
  }
  public Optional<User> getUserByOtp(int otp, User user) {
    return Optional.of(otpRepository.findByOtp(otp).getUser());
  }
  // Regex pattern to match
  private final String regex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&_+=()-])(?=\\S+$).{8,40}$";

  private String validatePasswordResetToken(String token) {
    PasswordResetToken passwordResetToken
            = passwordResetTokenRepository.findByToken(token);
    if (passwordResetToken == null) {
      return "Invalid";
    }
    Calendar cal = Calendar.getInstance();

    if (passwordResetToken.getExpirationTime().getTime()
            - cal.getTime().getTime() <= 0) {
      passwordResetTokenRepository.delete(passwordResetToken);
      return "Expired";
    }
    return "Valid";
  }

  private String validatePasswordResetOtp(int otp) {
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

  private void changePassword(User user, String newPassword) {
    user.setPassword(newPassword);
    userRepository.save(user);
  }


  private void createPasswordResetTokenForUser(User user, String rsToken) {
    PasswordResetToken passwordResetToken
            = new PasswordResetToken(rsToken, user);
    passwordResetTokenRepository.save(passwordResetToken);
  }

  @Override
  public ResponseEntity<?> resetPassword(@RequestBody ChangePasswordRequest password, HttpServletRequest request, PasswordResetToken resetToken) {
    User user = userRepository.findByEmail(password.getEmail());
    String token;
    if (user != null) {
      token = UUID.randomUUID().toString();
      createPasswordResetTokenForUser(user, token);
      passwordResetTokenMail(applicationUrl(request), token);
      passwordResetTokenRepository.deleteAll();
      applicationUrl(request);
//            SimpleMailMessage message = new SimpleMailMessage();
//            try {
//                message.setTo(password.getEmail());
//                message.setSubject("Limited time to 10 minutes. Click the link to Reset your Password: ");
//                message.setText("Hi, User.\n Forgot password?\n Here is the link to reset your password\n" + passwordResetTokenMail(applicationUrl(request), token) + "\nGood luck!");
//                mailSender.send(message);
//            } catch (Exception e) {
//                passwordResetTokenRepository.deleteByToken(token);
//                return ResponseEntity.ok("Invalid email address or mail server");
//            }
      return ResponseEntity.ok(new ResetPasswordResponse(token));
    }
    return ResponseEntity.badRequest().body("Wrong email address !");
  }

  private String passwordResetTokenMail(String applicationUrl, String token) {
    String url =
            applicationUrl
                    + "/auth/savePassword?token="
                    + token;

    log.info(url);
    return url;
  }
  private String applicationUrl(HttpServletRequest request) {
    return "http://localhost:3000" +
            request.getContextPath();
  }

//  private String applicationUrl(HttpServletRequest request) {
//    return "http://" +
//            request.getServerName() +
//            ":" +
//            request.getServerPort() +
//            request.getContextPath();
//  }

  @Override
  public ResponseEntity<?> resetPasswordOTP(ChangePasswordRequest password) {
    Twilio.init("AC428df5bd302a88e1e314d9ece0159181", "7fc4e2131a7bf04ea775faaf5ea8dee7");
    User user = userRepository.findByEmail(password.getEmail());
    Random r = new Random();
    int otpCode = 100000 + r.nextInt(888888);
    createPasswordResetOtp(user, otpCode);
    Message.creator(new PhoneNumber("+84866682422"),
            new PhoneNumber("+19497495157"),
            "Limited reset OTP code for 10 minutes: " + otpCode).create();
    return ResponseEntity.ok("OTP Send Successfully");
  }
  private void createPasswordResetOtp(User user, int otp) {
    Otp otpCode = new Otp(user, otp);
    otpRepository.save(otpCode);
  }

  @Override
  public String savePassword(@Valid @RequestParam("token") String token,
                             @Valid @RequestBody ChangePasswordRequest password){
    String result = validatePasswordResetToken(token);
    if(!result.equalsIgnoreCase("Valid")){
      return "Invalid Token";
    }
    Optional<User> user = getUserByPasswordResetToken(token, new User());
    if(user.isPresent()){
      Matcher matcher = Pattern.compile(regex).matcher(password.getNewPassword());
      if (matcher.find()){
        changePassword(user.get(), encoder.encode(password.getNewPassword()));
        passwordResetTokenRepository.deleteByToken(token);
        return "Password Reset Successfully !";}
      else { return "Password does not match wellFormed !";}
    } else {
      return "Invalid Token";
    }
  }

  @Override
  public String saveOtpPassword(@Valid @RequestParam("otp") int otp,
                                @Valid @RequestBody ChangePasswordRequest password) {
    String result = validatePasswordResetOtp(otp);
    if (!result.equalsIgnoreCase("Valid")) {
      return "Invalid OTP";
    }
    Optional<User> user = getUserByOtp(otp, new User());
    if(user.isPresent()){
      Matcher matcher = Pattern.compile(regex).matcher(password.getNewPassword());
      if(matcher.find()){
        changePassword(user.get(), encoder.encode(password.getNewPassword()));
        otpRepository.deleteBy(otp);
        return "Password Reset Successfully";}
      else { return "Password does not match wellFormed !";}
    } else {
      return "Invalid OTP";
    }
  }

}