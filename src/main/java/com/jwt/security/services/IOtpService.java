package com.jwt.security.services;

public interface IOtpService {
    String decodeStr (String decode);
    String encodeStr (String encode);
    String generateOtp();
    Long getOtpExpriedAt();

    boolean checkOtp(Long id);
}
