package com.example.runningservice.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PasswordRequestDto {
    private String oldPassword;
    private String newPassword;
    private String confirmPassword;
}
