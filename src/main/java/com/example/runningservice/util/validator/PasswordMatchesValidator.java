package com.example.runningservice.util.validator;

import com.example.runningservice.dto.SignupRequestDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, Object> {

    @Override
    public void initialize(PasswordMatches constraintAnnotation) {
    }

    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context) {
        SignupRequestDto signupRequestDto = (SignupRequestDto) obj;
        return signupRequestDto.getPassword().equals(signupRequestDto.getConfirmPassword());
    }
}
