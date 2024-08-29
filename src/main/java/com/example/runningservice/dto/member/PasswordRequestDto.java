package com.example.runningservice.dto.member;


import com.example.runningservice.util.validator.PasswordMatches;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@PasswordMatches(message = "새 비밀번호와 확인 비밀번호가 일치하지 않습니다.")
public class PasswordRequestDto {

    @NotBlank
    private String oldPassword;

    //영문+숫자+특수문자 포함, 8~50자
    @NotBlank
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,50}$", message = "비밀번호는 영문, 숫자, 특수문자를 포함하여 8 ~ 50자 이내여야 합니다.")
    private String newPassword;

    @NotBlank
    private String confirmNewPassword;
}
