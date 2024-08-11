package com.example.runningservice.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordRequestDto {
    private String oldPassword;
    private String newPassword;
    private String confirmPassword;
}
