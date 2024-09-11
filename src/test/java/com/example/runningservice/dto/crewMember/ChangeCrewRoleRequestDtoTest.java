package com.example.runningservice.dto.crewMember;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.runningservice.enums.CrewRole;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ChangeCrewRoleRequestDtoTest {
    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testValidRole() {
        // Given
        ChangeCrewRoleRequestDto dto = ChangeCrewRoleRequestDto.builder()
            .crewMemberId(1L)
            .newRole(CrewRole.STAFF)
            .build();

        // When
        Set<ConstraintViolation<ChangeCrewRoleRequestDto>> violations = validator.validate(dto);

        // Then
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("리더로 권한 변경 시 실패")
    public void testInvalidRole_LEADER() {
        // Given
        ChangeCrewRoleRequestDto dto = ChangeCrewRoleRequestDto.builder()
            .crewMemberId(1L)
            .newRole(CrewRole.LEADER)
            .build();

        // When
        Set<ConstraintViolation<ChangeCrewRoleRequestDto>> violations = validator.validate(dto);

        // Then
        assertFalse(violations.isEmpty());  // Validation error expected
        assertEquals("Invalid role", violations.iterator().next().getMessage());
    }
}