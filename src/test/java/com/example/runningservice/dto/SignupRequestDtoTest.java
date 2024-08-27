package com.example.runningservice.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.runningservice.enums.Gender;
import com.example.runningservice.enums.Region;
import com.example.runningservice.enums.Visibility;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
            .password("password!")
            .confirmPassword("password!")
            .phoneNumber("01012345678")
            .name("Kim")
            .nickName("Kim")
            .gender(Gender.MALE)
            .profileImage(null)
            .birthYear(1990)
            .activityRegion(Region.SEOUL)
            .birthYearVisibility(Visibility.PUBLIC)
            .nameVisibility(Visibility.PUBLIC)
            .genderVisibility(Visibility.PUBLIC)
            .phoneNumberVisibility(Visibility.PUBLIC)
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
            .profileImage(null)
            .birthYear(1990)
            .activityRegion(Region.SEOUL)
            .birthYearVisibility(Visibility.PUBLIC)
            .nameVisibility(Visibility.PUBLIC)
            .genderVisibility(Visibility.PUBLIC)
            .nameVisibility(Visibility.PUBLIC)
            .build();

        // When
        Set<ConstraintViolation<SignupRequestDto>> violations = validator.validate(
            signupRequestDto);

        // Then
        assertEquals(2, violations.size());

        // 모든 violation을 확인하면서 메시지와 필드를 검증
        for (ConstraintViolation<SignupRequestDto> violation : violations) {
            String message = violation.getMessage();
            String propertyPath = violation.getPropertyPath().toString();

            if (propertyPath.equals("confirmPassword")) {
                assertEquals("비밀번호가 일치하지 않습니다.", message);
            } else if (propertyPath.equals("password") || propertyPath.equals("confirmPassword")) {
                // 다른 유효성 검사 위반 메시지 확인 (예: NotBlank 등의 메시지)
                assertEquals("필수 정보입니다.", message);
            }
        }
    }

    @Test
    public void testInvalidPhoneNumber() {
        // Given
        SignupRequestDto signupRequestDto = SignupRequestDto.builder()
            .email("email@example.com")
            .password("Qwerqwer1!")
            .confirmPassword("Qwerqwer1!")
            .phoneNumber("010123456")
            .name("Kim")
            .nickName("Kim")
            .gender(Gender.MALE)
            .profileImage(null)
            .birthYear(1990)
            .activityRegion(Region.SEOUL)
            .birthYearVisibility(Visibility.PUBLIC)
            .nameVisibility(Visibility.PUBLIC)
            .genderVisibility(Visibility.PUBLIC)
            .phoneNumberVisibility(Visibility.PUBLIC)
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
            .name("")
            .nickName("Kim")
            .gender(Gender.MALE)
            .profileImage(null)
            .birthYear(1990)
            .activityRegion(Region.SEOUL)
            .birthYearVisibility(Visibility.PUBLIC)
            .nameVisibility(Visibility.PUBLIC)
            .genderVisibility(Visibility.PUBLIC)
            .phoneNumberVisibility(Visibility.PUBLIC)
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
            .nameVisibility(Visibility.PUBLIC)
            .birthYearVisibility(Visibility.PUBLIC)
            .phoneNumberVisibility(Visibility.PUBLIC)
            .genderVisibility(Visibility.PUBLIC)
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