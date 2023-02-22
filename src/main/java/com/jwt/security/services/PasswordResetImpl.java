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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.time.Instant;
import java.util.*;
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

  // Regex pattern to match
  private final String regex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&_+=()-])(?=\\S+$).{8,40}$";

  private String validatePasswordResetToken(String token) {
    PasswordResetToken passwordResetToken
            = passwordResetTokenRepository.findByToken(token);
    if (passwordResetToken == null) {
      return "Invalid";
    }
    Calendar cal = Calendar.getInstance();

    if (passwordResetToken.getExpirationTime().getTime() - cal.getTime().getTime() <= 0) {
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
      return "Expired";
    }
    return "Valid";
  }

  private void changeTokenPassword(String newPassword, User id, Long token) throws InterruptedException {
    Thread thread = new Thread(() -> userRepository.setNewPassword(newPassword, id.getId()));
      thread.start();
      thread.join();
    Thread thread1 = new Thread(() -> passwordResetTokenRepository.setExpiredTime(Date.from(Instant.now().plusMillis(-700000)), token));
      thread1.start();
  }
  private void changeOtpPassword(String newPassword, User id, Long otp) throws InterruptedException {
    Thread thread = new Thread(() -> userRepository.setNewPassword(newPassword, id.getId()));
    thread.start();
    thread.join();
    Thread thread1 = new Thread(() -> otpRepository.setExpiredTime(Date.from(Instant.now().plusMillis(-700000)), otp));
    thread1.start();
  }

  @CachePut(value = "token")
  public void createPasswordResetTokenForUser(User user, String rsToken) {
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
      Thread thread = new Thread(() -> createPasswordResetTokenForUser(user, token));
      thread.start();
      try {
        thread.join();
      } catch (InterruptedException e) {
        System.out.println(Arrays.toString(e.getStackTrace()));
      }
      passwordResetTokenMail(applicationUrl(request), token);
      Thread thread1 = new Thread(passwordResetTokenRepository::deleteAll);
      thread1.start();
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

  @CachePut(value = "otp")
  public void createPasswordResetOtp(User user, int otp) {
    Otp otpCode = new Otp(user, otp);
    otpRepository.save(otpCode);
  }

  @Override
  public ResponseEntity<?> resetPasswordOTP(ChangePasswordRequest password) {
    Twilio.init("AC428df5bd302a88e1e314d9ece0159181", "d60b5c6548496920f5d27bb9d2220bac");
    User user = userRepository.findByEmail(password.getEmail());
    int otpCode = 100000 + new Random().nextInt(888888);
    Thread thread = new Thread(() -> createPasswordResetOtp(user, otpCode));
      thread.start();
      try {
        thread.join();
      } catch (InterruptedException e) {
        System.out.println(Arrays.toString(e.getStackTrace()));
      }
    Thread thread1 = new Thread(otpRepository::deleteAllOtp);
      thread1.start();
    try {
      Message.creator(new PhoneNumber("+84866682422"),
              new PhoneNumber("+19497495157"),
              "Limited reset OTP code for 10 minutes: " + otpCode).create();
    } catch (Exception e) {
      System.out.println("Send SMS failed");
    }
    return ResponseEntity.ok("OTP Send Successfully");
  }

  @Override
  @CacheEvict(value = "token", beforeInvocation = false, key = "#token")
  public String savePassword(@Valid @RequestParam("token") String token,
                             @Valid @RequestBody ChangePasswordRequest password) {
    String result = validatePasswordResetToken(token);
    if(!result.equalsIgnoreCase("Valid")){
      return "Invalid Token";
    }
    Optional<User> user = Optional.ofNullable(passwordResetTokenRepository.findByToken(token).getUser());
    PasswordResetToken passwordResetToken
            = passwordResetTokenRepository.findByToken(token);
    if(user.isPresent()){
      Matcher matcher = Pattern.compile(regex).matcher(password.getNewPassword());
      if (matcher.find()){
        try {
          changeTokenPassword(encoder.encode(password.getNewPassword()), user.get(), passwordResetToken.getId());
        } catch (InterruptedException e) {
          System.out.println(Arrays.toString(e.getStackTrace()));
        }
        return "Password Reset Successfully !";}
      else { return "Password does not match wellFormed !";}
    } else {
      return "Invalid Token";
    }
  }

  @Override
  @CacheEvict(cacheNames = "otp", beforeInvocation = false, key = "#otp")
  public String saveOtpPassword(@Valid @RequestParam("otp") int otp,
                                @Valid @RequestBody ChangePasswordRequest password) {
    String result = validatePasswordResetOtp(otp);
    if (!result.equalsIgnoreCase("Valid")) {
      return "Invalid OTP";
    }
    Optional<User> user = Optional.ofNullable(otpRepository.findByOtp(otp).getUser());
    Otp otpCode = otpRepository.findByOtp(otp);
    if(user.isPresent()){
      Matcher matcher = Pattern.compile(regex).matcher(password.getNewPassword());
      if(matcher.find()){
        try {
          changeOtpPassword(encoder.encode(password.getNewPassword()), user.get(), otpCode.getId());
        } catch (InterruptedException e) {
          System.out.println(Arrays.toString(e.getStackTrace()));
        }
        return "Password Reset Successfully";}
      else { return "Password does not match wellFormed !";}
    } else {
      return "Invalid OTP";
    }
  }

}