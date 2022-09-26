package com.jwt.payload.request;

import javax.validation.constraints.NotBlank;
import java.lang.constant.Constable;

public class MailSender {

    @NotBlank
    public static String email;

    public static String password;

    public static Constable getEmail() {
        return email;
    }
}
