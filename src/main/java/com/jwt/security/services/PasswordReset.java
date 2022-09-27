package com.jwt.security.services;

import com.jwt.payload.request.ChangePasswordRequest;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

public interface PasswordReset {

    String resetPassword(@RequestBody ChangePasswordRequest passwordModel, HttpServletRequest request);

    String savePassword(@RequestParam("token") String token,
                        @RequestBody ChangePasswordRequest password);
}
