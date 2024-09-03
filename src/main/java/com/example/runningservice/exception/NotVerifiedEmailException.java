package com.example.runningservice.exception;

import lombok.Getter;
import org.springframework.security.core.AuthenticationException;

@Getter
public class NotVerifiedEmailException extends AuthenticationException {

    public NotVerifiedEmailException(String msg) {
        super(msg);
    }
}
