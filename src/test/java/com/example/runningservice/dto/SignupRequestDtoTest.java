package com.example.runningservice.dto;

import static org.junit.jupiter.api.Assertions.*;

import com.example.runningservice.enums.Gender;
import com.example.runningservice.enums.Region;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

class SignupRequestDtoTest {


    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testInvalidEmail() {
        // Given
        SignupRequestDto signupRequestDto = SignupRequestDto.builder()
            .email("invalid-email") // Invalid email format
            .password("Password123!")
            .confirmPassword("Password123!")
            .phoneNumber("01012345678")
            .name("Kim")
            .nickName("Kim")
            .gender(Gender.MALE)
            .birthYear(1990)
            .activityRegion(Region.SEOUL)
            .build();

        // When
        Set<ConstraintViolation<SignupRequestDto>> violations = validator.validate(
            signupRequestDto);

        // Then
        assertEquals(1, violations.size());
        ConstraintViolation<SignupRequestDto> violation = violations.iterator().next();
        assertEquals("올바른 형식의 이메일 주소여야 합니다", violation.getMessage());
        assertEquals("email", violation.getPropertyPath().toString());
    }

    @Test
    public void testInvalidPassword() {
        // Given
        SignupRequestDto signupRequestDto = SignupRequestDto.builder()
            .email("email@example.com")
            .password("password") // Invalid password format
            .confirmPassword("password")
            .phoneNumber("01012345678")
            .name("Kim")
            .nickName("Kim")
            .gender(Gender.MALE)
            .birthYear(1990)
            .activityRegion(Region.SEOUL)
            .build();

        // When
        Set<ConstraintViolation<SignupRequestDto>> violations = validator.validate(
            signupRequestDto);

        // Then
        assertEquals(1, violations.size());
        ConstraintViolation<SignupRequestDto> violation = violations.iterator().next();
        assertEquals("비밀번호는 영문, 숫자, 특수문자를 포함하여 8 ~ 50자 이내여야 합니다.", violation.getMessage());
        assertEquals("password", violation.getPropertyPath().toString());
    }

    @Test
    public void testConfirmPasswordNotMatching() {
        // Given
        SignupRequestDto signupRequestDto = SignupRequestDto.builder()
            .email("email@example.com")
            .password("Password123!")
            .confirmPassword("Password123!1")
            .phoneNumber("01012345678")
            .name("Kim")
            .nickName("Kim")
            .gender(Gender.MALE)
            .birthYear(1990)
            .activityRegion(Region.SEOUL)
            .build();

        // When
        Set<ConstraintViolation<SignupRequestDto>> violations = validator.validate(
            signupRequestDto);

        // Then
        assertEquals(1, violations.size());
        ConstraintViolation<SignupRequestDto> violation = violations.iterator().next();
        assertEquals("비밀번호가 일치하지 않습니다.", violation.getMessage());
        assertEquals(SignupRequestDto.class.getSimpleName(),
            violation.getRootBeanClass().getSimpleName());
    }

    @Test
    public void testInvalidPhoneNumber() {
        // Given
        SignupRequestDto signupRequestDto = SignupRequestDto.builder()
            .email("email@example.com")
            .password("Password123!")
            .confirmPassword("Password123!")
            .phoneNumber("invalid-phone-number") // Invalid phone number format
            .name("John Doe")
            .nickName("johndoe")
            .gender(Gender.MALE)
            .birthYear(1990)
            .activityRegion(Region.SEOUL)
            .build();

        // When
        Set<ConstraintViolation<SignupRequestDto>> violations = validator.validate(signupRequestDto);

        // Then
        assertEquals(1, violations.size());
        ConstraintViolation<SignupRequestDto> violation = violations.iterator().next();
        assertEquals("휴대전화번호 형식이 올바르지 않습니다.", violation.getMessage());
        assertEquals("phoneNumber", violation.getPropertyPath().toString());
    }

    @Test
    public void testInvalidName() {
        // Given
        SignupRequestDto signupRequestDto = SignupRequestDto.builder()
            .email("email@example.com")
            .password("Password123!")
            .confirmPassword("Password123!")
            .phoneNumber("01012345678")
            .name("") // Invalid name (empty)
            .nickName("johndoe")
            .gender(Gender.MALE)
            .birthYear(1990)
            .activityRegion(Region.SEOUL)
            .build();

        // When
        Set<ConstraintViolation<SignupRequestDto>> violations = validator.validate(signupRequestDto);

        // Then
        assertEquals(2, violations.size());

        boolean isNameEmptyViolationFound = false;
        boolean isNameSizeViolationFound = false;

        for (ConstraintViolation<SignupRequestDto> violation : violations) {
            String message = violation.getMessage();
            String propertyPath = violation.getPropertyPath().toString();

            if (propertyPath.equals("name")) {
                if (message.equals("이름을 입력해주세요.")) {
                    isNameEmptyViolationFound = true;
                } else if (message.equals("크기가 2에서 12 사이여야 합니다")) {
                    isNameSizeViolationFound = true;
                }
            }
        }

        assertTrue(isNameEmptyViolationFound);
        assertTrue(isNameSizeViolationFound);
    }

    @Test
    public void testInvalidBirthYear() {
        // Given
        SignupRequestDto signupRequestDto = SignupRequestDto.builder()
            .email("email@example.com")
            .password("Password123!")
            .confirmPassword("Password123!")
            .phoneNumber("01012345678")
            .name("John Doe")
            .nickName("johndoe")
            .gender(Gender.MALE)
            .birthYear(1800) // Invalid birth year (too early)
            .activityRegion(Region.SEOUL)
            .build();

        // When
        Set<ConstraintViolation<SignupRequestDto>> violations = validator.validate(signupRequestDto);

        // Then
        assertEquals(1, violations.size());
        ConstraintViolation<SignupRequestDto> violation = violations.iterator().next();
        assertEquals("birthYear", violation.getPropertyPath().toString());
        assertEquals("1900 ~ 현재 연도 이내 값을 입력하세요.", violation.getMessage());
    }
}