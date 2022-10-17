package com.jwt.payload.request;

import com.jwt.models.User;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@NoArgsConstructor
public class Otp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int otp;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id",
            nullable = false)
    private User user;

    public Otp(User user, int otp) {
        super();
        this.user = user;
        this.otp = otp;
    }
}
