package com.jwt.exception;

import lombok.Getter;

import java.io.Serial;

@Getter
public class BadRequestException extends RuntimeException {

    String code;

    /**
     *
     */
    @Serial
    private static final long serialVersionUID = -5614481280002973837L;

    public BadRequestException() {
        super("Invalid request.");
    }

    public BadRequestException(String message) {
        super(message);
    }

    public BadRequestException(String message, String code) {
        super(message);
        this.code = code;
    }
}
