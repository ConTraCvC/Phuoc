package com.jwt.models;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Calendar;
import java.util.Date;

@Data
@Entity
@NoArgsConstructor
public class Otp {

  private static final int EXPIRATION_TIME = 10;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(nullable = false, unique = true)
  private int otp;
  @Column(nullable = false)
  private Date realTime;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id",
          nullable = false)
  private User user;

  public Otp(User user, int otp) {
    super();
    this.user = user;
    this.otp = otp;
    this.realTime = calculateDate();
  }

  private Date calculateDate() {
    Calendar calendar = Calendar.getInstance();
    calendar.setTimeInMillis(new Date().getTime());
    calendar.add(Calendar.MINUTE, Otp.EXPIRATION_TIME);
    return new Date(calendar.getTime().getTime());
  }
}
