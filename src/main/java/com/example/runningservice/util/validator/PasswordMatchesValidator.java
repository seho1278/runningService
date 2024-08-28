package com.example.runningservice.util.validator;

import com.example.runningservice.dto.SignupRequestDto;
import com.example.runningservice.dto.member.PasswordRequestDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, Object> {

    @Override
    public void initialize(PasswordMatches constraintAnnotation) {
    }

    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context) {
        if (obj instanceof SignupRequestDto) {
            SignupRequestDto signupRequestDto = (SignupRequestDto) obj;
            return signupRequestDto.getPassword().equals(signupRequestDto.getConfirmPassword());
        } else if (obj instanceof PasswordRequestDto) {
            PasswordRequestDto passwordRequestDto = (PasswordRequestDto) obj;
            return passwordRequestDto.getNewPassword().equals(passwordRequestDto.getConfirmNewPassword());
        }
        return false;
    }
}
