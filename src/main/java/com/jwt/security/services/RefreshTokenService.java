package com.jwt.security.services;

import com.jwt.exception.TokenRefreshException;
import com.jwt.models.RefreshToken;
import com.jwt.repository.RefreshTokenRepository;
import com.jwt.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

  @Value("${jwtRefreshExpirationMs}")
  private Long refreshTokenDurationMs;
  private final RefreshTokenRepository refreshTokenRepository;
  private final UserRepository userRepository;

  public RefreshToken createRefreshToken(Long userId) {
    RefreshToken refreshToken = new RefreshToken();

    refreshToken.setUser(userRepository.findById(userId).isPresent() ? userRepository.findById(userId).get() : null);
    refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
    refreshToken.setToken(UUID.randomUUID().toString());

    return refreshToken;
  }

  public RefreshToken verifyExpiration(RefreshToken token) {
    if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
      refreshTokenRepository.delete(token);
      throw new TokenRefreshException(token.getToken(), "Refresh token was expired. Please make a new signin request");
    }
    return token;
  }
}
