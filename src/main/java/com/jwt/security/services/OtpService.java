package com.jwt.security.services;

import com.jwt.payload.request.Otp;
import com.jwt.repository.OtpRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Date;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
public class OtpService implements IOtpService{

    @Value("${otp.expried.in}")
    private Long otpExpriedIn;

    @Value("${otp.max.length}")
    private int otpMaxLength;

    private final OtpRepository otpRepo;

    @Override
    public String decodeStr(String decode) {
        byte[] decodedBytes = Base64.getDecoder().decode(decode);
        return new String(decodedBytes);
    }

    @Override
    public String encodeStr(String encode) {
        return Base64.getDecoder().toString();
    }

    @Override
    public String generateOtp() {
        AtomicReference<StringBuilder> generateOTP = new AtomicReference<>(new StringBuilder());
        SecureRandom secureRandom = new SecureRandom();
        try {
            secureRandom = SecureRandom.getInstance(secureRandom.getAlgorithm());
            generateOTP.get().append(secureRandom.nextInt(999999) + otpMaxLength);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return generateOTP.toString();
    }

    @Override
    public Long getOtpExpriedAt() {
        return new Date().getTime() + TimeUnit.MINUTES.toMillis(otpExpriedIn);
    }

    @Override
    public boolean checkOtp(Long id) {
        Optional<Otp> obj = otpRepo.findById(id);
        if (obj.isPresent()) {
            Long currentTime = System.currentTimeMillis();
            return getOtpExpriedAt() - currentTime > 0;
        } else otpRepo.deleteById(id);
        return false;
    }
}
