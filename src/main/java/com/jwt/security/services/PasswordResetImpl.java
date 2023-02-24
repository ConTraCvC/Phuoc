package com.jwt.security.services;

import com.jwt.models.PasswordResetToken;
import com.jwt.models.User;
import com.jwt.payload.request.ChangePasswordRequest;
import com.jwt.models.Otp;
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
import org.springframework.mail.SimpleMailMessage;
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

  @CachePut(value = "otp")
  public PasswordResetToken createPasswordResetTokenForUser(Long userId) {
    PasswordResetToken token = new PasswordResetToken();
    token.setUser(userRepository.findById(userId).isPresent() ? userRepository.findById(userId).get() : null);
    token.setExpirationTime(Date.from(Instant.now().plusMillis(600000)));
    token.setToken(UUID.randomUUID().toString());
    return token;
  }

  @Override
  public ResponseEntity<?> resetPassword(@RequestBody ChangePasswordRequest password, HttpServletRequest request, PasswordResetToken tokenRS) {
    Optional<User> user = userRepository.findByEmail(password.getEmail());
    if (user.isPresent()) {
      Optional<PasswordResetToken> token = passwordResetTokenRepository.findByUserId(user.get().getId());
      String tokenCode = UUID.randomUUID().toString();
      try {
        if(token.isPresent()){
          passwordResetTokenRepository.updateToken(tokenCode, Date.from(Instant.now().plusMillis(600000)), user.get().getId());
        } else {
          passwordResetTokenRepository.save(createPasswordResetTokenForUser(user.get().getId()));
        }
        passwordResetTokenMail(applicationUrl(request), tokenCode);
        applicationUrl(request);
        return ResponseEntity.ok(tokenCode);
      } catch (Exception e) {
        return ResponseEntity.badRequest().body("Set resetToken failed");
      }
    }
//    SimpleMailMessage message = new SimpleMailMessage();
//    try {
//      message.setTo(password.getEmail());
//      message.setSubject("Limited time to 10 minutes. Click the link to Reset your Password: ");
//      message.setText("Hi, User.\n Forgot password?\n Here is the link to reset your password\n"
//              + passwordResetTokenMail(applicationUrl(request), tokenRS.getToken()) + "\nGood luck!");
//      mailSender.send(message);
//    } catch (Exception e) {
//      return ResponseEntity.badRequest().body("Invalid email address or mail server");
//    }
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
  public Otp createOtpToken(Long userId) {
    Otp otp = new Otp();
    otp.setUser(userRepository.findById(userId).isPresent() ? userRepository.findById(userId).get() : null);
    otp.setRealTime(Date.from(Instant.now().plusMillis(600000)));
    otp.setOtp(100000 + new Random().nextInt(888888));
    return otp;
  }

  @Override
  public ResponseEntity<?> resetPasswordOTP(ChangePasswordRequest password, Otp otpCodex) {
    Twilio.init("AC428df5bd302a88e1e314d9ece0159181", "d60b5c6548496920f5d27bb9d2220bac");
    Optional<User> user = userRepository.findByEmail(password.getEmail());
    if(user.isPresent()) {
      Optional<Otp> otp = otpRepository.findByUserId(user.get().getId());
      int otpCode = 100000 + new Random().nextInt(888888);
      try {
        if (otp.isPresent()) {
          otpRepository.updateOtp(otpCode, Date.from(Instant.now().plusMillis(600000)), user.get().getId());
        } else {
          otpRepository.save(createOtpToken(user.get().getId()));
        }
        return ResponseEntity.ok("Successfully");
      } catch (Exception e) {
        return ResponseEntity.badRequest().body("Set OtpToken failed");
      }
    }
//    try {
//      Message.creator(new PhoneNumber("+84866682422"),
//              new PhoneNumber("+19497495157"),
//              "Limited reset OTP code for 10 minutes: " + otpCodex.getOtp()).create();
//    } catch (Exception e) {
//      return ResponseEntity.badRequest().body("Send SMS failed");
//    }
    return ResponseEntity.badRequest().body("Wrong email address");
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
          return "Something went wrong!";
        }
        return "Password Reset Successfully !";}
      else { return "Password does not match wellFormed !";}
    } else {
      return "Invalid Token";
    }
  }

  @Override
  @CacheEvict(cacheNames = "otp", key = "#otp")
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
          return "Something went wrong!";
        }
        return "Password Reset Successfully";}
      else { return "Password does not match wellFormed !";}
    } else {
      return "Invalid OTP";
    }
  }

}