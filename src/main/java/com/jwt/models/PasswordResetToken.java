package com.jwt.models;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Calendar;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
public class PasswordResetToken {
  private static final int EXPIRATION_TIME = 10;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(nullable = false, unique = true)
  private String token;
  @Column(nullable = false)
  private Date expirationTime;

  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "user_id",
          nullable = false)
//            foreignKey = @ForeignKey(name = "FK_USER_PASSWORD_TOKEN"))

  private User user;

  public PasswordResetToken(String token, User user) {
    super();
    this.token = token;
    this.user = user;
    this.expirationTime = calculateExpirationDate();
  }

  private Date calculateExpirationDate() {
    Calendar calendar = Calendar.getInstance();
    calendar.setTimeInMillis(new Date().getTime());
    calendar.add(Calendar.MINUTE, PasswordResetToken.EXPIRATION_TIME);
    return new Date(calendar.getTime().getTime());
  }
}
